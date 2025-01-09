package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response

public class OpenAiChaiApiProvider(
    public val clientConfig: OpenAIConfig,
    public val generationConfig: OpenAiGenerationConfig
) : ChatApiProvider<ChatCompletion> {
    public val client: OpenAI = OpenAI(clientConfig)
    override suspend fun generate(context: Context): Response<ChatCompletion> {
        val messageList = mutableListOf<ChatMessage>()
        context.systemInstruction?.let {
            ChatMessage(
                role = Role.System,
                content = it
            )
        }?.let { messageList.add(it) }
        context.history.asSequence().map {
            ChatMessage(
                role = it.sender.toOpenAiRole(),
                content = it.content
            )
        }.forEach { messageList.add(it) }


        val chatCompletion = client.chatCompletion(
            with(generationConfig) {
                ChatCompletionRequest(
                    model = ModelId(model),
                    messages = messageList,
                    temperature = temperature,
                    topP = topP,
                    stop = stop,
                    maxTokens = maxTokens,
                    presencePenalty = presencePenalty,
                    frequencyPenalty = frequencyPenalty,
                    logitBias = logitBias,
                    user = user,
                    seed = seed,
                    logprobs = logprobs,
                    topLogprobs = topLogprobs,
                    instanceId = instanceId,
                )
            }
        )
        return Response(
            chatCompletion.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("No legal response message found"),
            chatCompletion
        )
    }
}