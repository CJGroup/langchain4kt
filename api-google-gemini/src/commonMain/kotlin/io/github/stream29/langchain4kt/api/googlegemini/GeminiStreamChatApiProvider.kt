package io.github.stream29.langchain4kt.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.APIController
import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.common.RequestOptions
import dev.shreyaspatil.ai.client.generativeai.common.client.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.common.shared.SafetySetting
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.GenerationException
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.*
import kotlin.concurrent.Volatile

/**
 * [StreamChatApiProvider] using Google Gemini API with specific parameters.
 *
 * @property modelName the model to use for generation.
 * @property apiKey the API key to use for authentication. Get it at [Google AI Studio](https://aistudio.google.com).
 * @property generationConfig the configuration for generation.
 * @property safetySettings the safety settings for generation.
 * @property timeoutMillis the timeout for the request, null for max.
 * @property apiVersion the API version to use.
 * @property endpoint the API endpoint to use.
 */
public data class GeminiStreamChatApiProvider(
    val modelName: String,
    val apiKey: String,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null,
    val timeoutMillis: Long? = null,
    val apiVersion: String = "v1beta",
    val endpoint: String = "https://generativelanguage.googleapis.com",
) : StreamChatApiProvider<GenerateContentResponse> {
    private val requestOptions: RequestOptions = RequestOptions(timeoutMillis, apiVersion, endpoint)
    private val controller = APIController(
        apiKey,
        modelName,
        requestOptions,
        "genai-android"
    )

    override suspend fun generate(context: Context): StreamResponse<GenerateContentResponse> {
        val currentResponse = VolatileReference<GenerateContentResponse?>(null)
        val deferredMetaInfo = CompletableDeferred<GenerateContentResponse>()
        val flow = try {
            controller.generateContentStream(
                constructRequest(
                    modelName,
                    context,
                    safetySettings,
                    generationConfig
                )
            ).onEach {
                currentResponse.value = it
            }.map {
                it.text
            }.onCompletion {
                if (it != null) {
                    deferredMetaInfo.completeExceptionally(it)
                } else {
                    if (currentResponse.value != null) {
                        deferredMetaInfo.complete(currentResponse.value!!)
                    } else {
                        deferredMetaInfo.completeExceptionally(GenerationException("Response not found."))
                    }
                }
            }.filterNotNull().catch {
                throw GenerationException("Generation failed with context: $context", it)
            }
        } catch (e: Throwable) {
            deferredMetaInfo.completeExceptionally(e)
            throw GenerationException("Generation failed with context: $context", e)
        }
        return StreamResponse(flow, deferredMetaInfo)
    }
}

private data class VolatileReference<T>(@Volatile var value: T)