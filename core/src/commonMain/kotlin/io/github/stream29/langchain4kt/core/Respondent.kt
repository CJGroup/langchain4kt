package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.message.MessageSender

interface Respondent {
    suspend fun chat(message: String): String
}

data class SimpleRespondent(
    val apiProvider: ChatApiProvider<*>,
    val systemInstruction: String? = null
) : Respondent {
    override suspend fun chat(message: String): String {
        return apiProvider.generateFrom {
            systemInstruction(systemInstruction)
            MessageSender.User.chat(message)
        }.message
    }
}

fun ChatApiProvider<*>.toRespondent(systemInstruction: String? = null) =
    SimpleRespondent(this, systemInstruction)

fun ChatModel.toRespondent() =
    SimpleRespondent(apiProvider, context.systemInstruction)