package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.concurrent.Volatile

public interface StreamChatModel {
    public val context: Context
    public val isReady: Boolean
    public suspend fun chat(message: String): Flow<String>
}

public fun <T> StreamChatApiProvider<T>.asStreamChatModel(context: ContextBuilder.() -> Unit = {}): SimpleStreamChatModel =
    SimpleStreamChatModel(this, context)

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

    override suspend fun chat(message: String): Flow<String> = coroutineScope{
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
                        rollback()
                        throw it
                    }
                }
        } catch (e: Exception) {
            rollback()
            throw e
        }
    }
}
