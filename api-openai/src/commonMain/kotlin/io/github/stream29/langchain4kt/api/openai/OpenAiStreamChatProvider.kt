package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 * Implementation of [StreamChatApiProvider] for OpenAI Chat API.
 *
 * The meta info in the response is the last [ChatCompletionChunk] returned by OpenAI API.
 */
public class OpenAiStreamChatProvider(
    public val clientConfig: OpenAIConfig,
    public val generationConfig: OpenAiGenerationConfig,
    public val requestOptions: RequestOptions = RequestOptions(),
) : StreamChatApiProvider<ChatCompletionChunk> {
    public val client: OpenAI = OpenAI(clientConfig)
    override suspend fun generate(context: Context): StreamResponse<ChatCompletionChunk> {
        var currentMetaInfo: ChatCompletionChunk? = null
        val lastMetaInfo = CompletableDeferred<ChatCompletionChunk>()
        val flow = client.chatCompletions(
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
        ).onEach {
            currentMetaInfo = it
        }.map {
            it.choices.firstOrNull()?.delta?.content
                ?: throw IllegalStateException("No legal response message found")
        }.onCompletion {
            lastMetaInfo.complete(currentMetaInfo!!)
        }
        return StreamResponse(flow, lastMetaInfo)
    }
}