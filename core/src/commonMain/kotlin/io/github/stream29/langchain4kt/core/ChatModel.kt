package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.GenerationException

interface ChatModel {
    val context: Context
    val apiProvider: ChatApiProvider<*>
    suspend fun chat(message: String): String
}

fun ChatApiProvider<*>.asChatModel(context: ContextBuilder.() -> Unit = {}) =
    SimpleChatModel(this, context)

data class SimpleChatModel<MetaInfo>(
    override var apiProvider: ChatApiProvider<MetaInfo>,
    override val context: Context = Context()
) : ChatModel {
    constructor(
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