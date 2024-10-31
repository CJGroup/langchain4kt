package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatModel
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender

class IteratedPromptModel(
    val baseModel: ChatModel,
    var prompt: String = "",
    val onMessage: (message: Message, oldPrompt: String) -> String
) : ChatModel by baseModel {
    init {
        prompt = context.history.fold(prompt) { acc, message -> onMessage(message, acc) }
    }

    override suspend fun chat(message: String): String {
        context.add { MessageSender.User.chat(message) }
        val newPrompt = onMessage(Message(MessageSender.User, message), prompt)
        val response = apiProvider.generate(Context.of { MessageSender.User.chat(newPrompt) }).message
        context.add { MessageSender.Model.chat(response) }
        prompt = onMessage(Message(MessageSender.Model, response), newPrompt)
        return response
    }
}