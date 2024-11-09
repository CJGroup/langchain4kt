package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatModel
import io.github.stream29.langchain4kt.core.Respondent
import io.github.stream29.langchain4kt.core.dsl.add
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.GenerationException

public class IteratedPromptModel(
    public val respondent: Respondent,
    override val context: Context = Context(),
    public var prompt: String = "",
    public val onMessagePrompt: (message: Message, oldPrompt: String) -> String
) : ChatModel {
    override suspend fun chat(message: String): String {
        val promptBackup = prompt
        val historyLengthBackup = context.history.size
        try {
            context.add { MessageSender.User.chat(message) }
            onMessage(Message(MessageSender.User, message), prompt)
            val response = respondent.chat(prompt)
            context.add { MessageSender.Model.chat(response) }
            onMessage(Message(MessageSender.Model, response), prompt)
            return response
        } catch (e: Exception) {
            val generationException = GenerationException("Generation failed with prompt $prompt", e)
            while (context.history.size > historyLengthBackup) {
                context.history.removeLast()
            }
            prompt = promptBackup
            throw generationException
        }
    }

    private suspend fun onMessage(message: Message, oldPrompt: String) {
        prompt = respondent.chat(onMessagePrompt(message, oldPrompt))
    }
}