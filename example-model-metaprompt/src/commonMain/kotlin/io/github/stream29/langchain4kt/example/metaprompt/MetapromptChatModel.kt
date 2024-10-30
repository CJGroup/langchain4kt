package io.github.stream29.langchain4kt.example.metaprompt

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.ChatModel
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender

class MetapromptChatModel(
    override val context: Context = Context(),
    var apiProvider: ChatApiProvider<*>,
    var metapromptTransform: (String) -> String
) : ChatModel {
    override suspend fun chat(message: String): String {
        val metaprompt = metapromptTransform(message)
        val prompt =
            apiProvider.generate(
                Context.of {
                    MessageSender.User.chat(metaprompt)
                }
            ).message

        val response = apiProvider.generate(
            context.copy().apply { add { MessageSender.User.chat(prompt) } }
        ).message

        context.add {
            MessageSender.User.chat(message)
            MessageSender.Model.chat(response)
        }
        return response
    }
}