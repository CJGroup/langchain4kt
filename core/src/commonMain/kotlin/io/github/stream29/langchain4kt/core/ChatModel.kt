package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.GenerationException

/**
 * A chat model that records its own historical [context].
 *
 * You can simply chat with [String] message. Every message will produce a response in [String].
 *
 * It does not prove thread safety. Please do not use it in concurrency environment.
 */
public interface ChatModel {
    public val context: Context
    public suspend fun chat(message: String): String
}

/**
 * Build a [SimpleChatModel] with a [ChatApiProvider].
 *
 * @param context Building the initial context of the chat model.
 */
public fun <T> ChatApiProvider<T>.asChatModel(context: ContextBuilder.() -> Unit = {}): SimpleChatModel<T> =
    SimpleChatModel(this, context)

/**
 * A simple chat model that uses a [ChatApiProvider] to generate responses.
 *
 * It simply adds the message into the context,
 * generates a response with [apiProvider],
 * and adds it into [context].
 *
 * It does not record any [MetaInfo].
 */
public data class SimpleChatModel<MetaInfo>(
    var apiProvider: ChatApiProvider<MetaInfo>,
    override val context: Context = Context()
) : ChatModel {
    public constructor(
        apiProvider: ChatApiProvider<MetaInfo>,
        context: ContextBuilder.() -> Unit
    ) : this(apiProvider, Context.of(context))

    override suspend fun chat(message: String): String {
        try {
            context.add {
                MessageSender.User.chat(message)
            }
            val response = apiProvider.generate(context).message
            context.add {
                MessageSender.Model.chat(response)
            }
            return response
        } catch (e: Exception) {
            val failInfo = "Generation failed with context $context"
            context.history.removeLast()
            throw GenerationException(failInfo, e)
        }
    }
}