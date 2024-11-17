package io.github.stream29.langchain4kt.streaming

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.GenerationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.concurrent.Volatile

/**
 * A chat model that records its own historical [context].
 *
 * You can simply chat with [String] message. Every message will produce a response in [Flow] of [String].
 *
 * It should provide **strong exception safety guarantee** that when [chat] throws a exception, the state of itself **should not** change
 *
 * When [chat] is called, it will wait until the previous chat is finished.
 *
 * It is **not** concurrency safe.
 */
public interface StreamChatModel {
    public val context: Context
    public val isReady: Boolean
    public suspend fun chat(message: String): Flow<String>
}

/**
 * Build a [SimpleStreamChatModel] with a [StreamChatApiProvider].
 *
 * @param context Building the initial context of the chat model.
 */
public fun <T> StreamChatApiProvider<T>.asStreamChatModel(context: ContextBuilder.() -> Unit = {}): SimpleStreamChatModel =
    SimpleStreamChatModel(this, context)

/**
 * A simple chat model that uses a [ChatApiProvider] to generate responses.
 *
 * It simply adds the message into the context,
 * generates a response with [apiProvider],
 * and adds it into [context].
 *
 * It does not record any `MetaInfo`.
 */
public data class SimpleStreamChatModel(
    public val apiProvider: StreamChatApiProvider<*>,
    override val context: Context
) : StreamChatModel {

    public constructor(
        apiProvider: StreamChatApiProvider<*>,
        context: ContextBuilder.() -> Unit
    ) : this(apiProvider, Context.of(context))

    @Volatile
    private var _isReady = true
    override val isReady: Boolean
        get() = _isReady

    override suspend fun chat(message: String): Flow<String> = coroutineScope {
        while (!_isReady) {
            delay(100)
        }
        val contextBackup = context.history.size
        fun rollback() {
            while (context.history.size > contextBackup)
                context.history.removeAt(context.history.size - 1) // I don't know why, but when I use removeLast I get deadlocked
            _isReady = true
        }
        try {
            context.add { MessageSender.User.chat(message) }
            _isReady = false
            val stringBuilder = StringBuilder()
            apiProvider
                .generate(context).message
                .onEach { stringBuilder.append(it) }
                .onCompletion {
                    if (it == null) {
                        context.add { MessageSender.Model.chat(stringBuilder.toString()) }
                        _isReady = true
                    } else {
                        val failInfo = "Generation failed with context $context"
                        rollback()
                        throw GenerationException(failInfo, it)
                    }
                }
        } catch (e: Exception) {
            val failInfo = "Generation failed with context $context"
            rollback()
            throw GenerationException(failInfo, e)
        }
    }
}
