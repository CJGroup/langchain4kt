package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response

public class OpenAiChaiApiProvider(
    public val openAiClient: OpenAI,
    public val model: String
) : ChatApiProvider<ChatCompletion> {
    override suspend fun generate(context: Context): Response<ChatCompletion> {
        val messageList = context.systemInstruction?.let {
            ChatMessage(
                role = Role.System,
                content = it
            )
        }.let { sequenceOf(it) }.plus(context.history.asSequence().map {
            ChatMessage(
                role = it.sender.toOpenAiRole(),
                content = it.content
            )
        }).filterNotNull().toList()
        val chatCompletion = openAiClient.chatCompletion(
            ChatCompletionRequest(
                model = ModelId(model),
                messages = messageList
            )
        )
        return Response(
            chatCompletion.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("No legal response message found"),
            chatCompletion
        )
    }
}