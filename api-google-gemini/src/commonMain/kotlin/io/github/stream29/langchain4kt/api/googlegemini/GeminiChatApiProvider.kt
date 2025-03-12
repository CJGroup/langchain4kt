package io.github.stream29.langchain4kt.api.googlegemini

import dev.shreyaspatil.ai.client.generativeai.common.APIController
import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentRequest
import dev.shreyaspatil.ai.client.generativeai.common.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.common.RequestOptions
import dev.shreyaspatil.ai.client.generativeai.common.client.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.common.shared.Content
import dev.shreyaspatil.ai.client.generativeai.common.shared.SafetySetting
import io.github.stream29.langchain4kt.core.ApiProvider
import io.github.stream29.langchain4kt.core.History
import kotlinx.serialization.ExperimentalSerializationApi

/**
 *
 * @property modelName the model to use for generation.
 * @property apiKey the API key to use for authentication. Get it at [Google AI Studio](https://aistudio.google.com).
 * @property generationConfig the configuration for generation.
 * @property safetySettings the safety settings for generation.
 * @property timeoutMillis the timeout for the request, null for max.
 * @property apiVersion the API version to use.
 * @property endpoint the API endpoint to use.
 */
@OptIn(ExperimentalSerializationApi::class)
public data class GeminiApiProvider(
    val modelName: String,
    val apiKey: String,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null,
    val timeoutMillis: Long? = null,
    val apiVersion: String = "v1beta",
    val endpoint: String = "https://generativelanguage.googleapis.com",
) : ApiProvider<Content, GenerateContentResponse> {
    private val requestOptions: RequestOptions = RequestOptions(timeoutMillis, apiVersion, endpoint)
    private val controller = APIController(
        apiKey,
        modelName,
        requestOptions,
        "genai-android"
    )

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun invoke(p1: History<Content>): GenerateContentResponse {
        val systemInstruction = p1.firstOrNull()?.takeIf { it.role == "system" }
        val contents = if (systemInstruction != null) p1.drop(1) else p1
        return controller.generateContent(
            GenerateContentRequest(
                modelName,
                contents,
                safetySettings,
                generationConfig,
                null,
                null,
                systemInstruction
            )
        )
    }
}

