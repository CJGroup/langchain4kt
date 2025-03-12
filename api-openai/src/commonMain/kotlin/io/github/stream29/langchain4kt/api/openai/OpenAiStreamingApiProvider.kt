package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.github.stream29.langchain4kt.core.History
import io.github.stream29.langchain4kt.core.StreamingApiProvider
import kotlinx.coroutines.flow.Flow


public class OpenAiStreamingApiProvider(
    public val clientConfig: OpenAIConfig,
    public val generationConfig: OpenAiGenerationConfig,
    public val requestOptions: RequestOptions = RequestOptions(),
) : StreamingApiProvider<ChatMessage, ChatCompletionChunk> {
    public val client: OpenAI = OpenAI(clientConfig)
    override suspend fun invoke(p1: History<ChatMessage>): Flow<ChatCompletionChunk> {
        return client.chatCompletions(
            with(generationConfig) {
                ChatCompletionRequest(
                    model = ModelId(model),
                    messages = p1,
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
    }
}