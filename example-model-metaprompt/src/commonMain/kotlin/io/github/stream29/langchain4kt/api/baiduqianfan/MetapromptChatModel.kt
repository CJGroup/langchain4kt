package io.github.stream29.langchain4kt.api.baiduqianfan

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.ChatLanguageModel
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.message.TextMessage
import io.github.stream29.langchain4kt.core.output.Response

class MetapromptChatModel(
    override val context: Context = Context(),
    var apiProvider: ChatApiProvider<*, *>,
    var metapromptTransform: (String) -> String
) : ChatLanguageModel<Unit, Unit> {
    override suspend fun <Content> chat(message: Message<Content>): Response<Message<*>, Unit, Unit> {
        val metaprompt = metapromptTransform(message.content as String)
        val prompt =
            apiProvider.generate(
                context.copy(
                    history = mutableListOf(
                        TextMessage(
                            MessageSender.User,
                            metaprompt
                        )
                    )
                )
            ).let {
                it as Response.Success<Message<String>, *>
            }.content.content
        val response = apiProvider.generate(
            context.copy().also {
                it.history.add(
                    TextMessage(
                        MessageSender.User,
                        prompt
                    )
                )
            }
        ) as Response.Success<Message<String>, Unit>
        context.history.add(message)
        context.history.add(TextMessage(MessageSender.Model, response.content.content))
        return response
    }
}