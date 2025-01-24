package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response

/**
 * Implementation of [ChatApiProvider] for OpenAI Chat API.
 */
public class OpenAiChatApiProvider(
    public val clientConfig: OpenAIConfig,
    public val generationConfig: OpenAiGenerationConfig,
    public val requestOptions: RequestOptions = RequestOptions(),
) : ChatApiProvider<ChatCompletion> {
    public val client: OpenAI = OpenAI(clientConfig)
    override suspend fun generate(context: Context): Response<ChatCompletion> {
        val chatCompletion = client.chatCompletion(
            with(generationConfig) {
                ChatCompletionRequest(
                    model = ModelId(model),
                    messages = context.toOpenAiMessageList(),
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
            },
            requestOptions
        )
        return Response(
            chatCompletion.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("No legal response message found"),
            chatCompletion
        )
    }
}