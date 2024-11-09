package io.github.stream29.langchain4kt.api.googlegemini

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.json.Json

/**
 * Providing access to Google Gemini API.
 */
public data class GeminiApiProvider(
    val httpClient: HttpClient,
    val model: String,
    val apiKey: String,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig()
) : ChatApiProvider<GeminiResponse> {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun generate(context: Context): Response<GeminiResponse> {
        val bodyAsText = httpClient.post("https://generativelanguage.googleapis.com/v1beta/models/") {
            configureRequestBy(context)
        }.bodyAsText(fallbackCharset = Charsets.UTF_8)
        runCatching {
            val geminiResponse = json.decodeFromString<GeminiResponse>(bodyAsText)
            return Response(
                message = geminiResponse.candidates
                    .first()
                    .content
                    .text,
                metaInfo = geminiResponse
            )
        }
        throw RuntimeException("Unexpected response: $bodyAsText")
    }

    private fun HttpRequestBuilder.configureRequestBy(context: Context) {
        url {
            appendPathSegments("$model:generateContent")
            parameters.append("key", apiKey)
        }
        contentType(ContentType.Application.Json)
        setBody(
            GeminiRequest(
                contents = context.history.asSequence()
                    .map {
                        GeminiContent(
                            listOf(mutableMapOf("text" to it.content)),
                            it.sender.toString()
                        )
                    }.toMutableList(),
                generationConfig = generationConfig,
                systemInstruction = context.systemInstruction?.let {
                    GeminiContent(
                        listOf(mutableMapOf("text" to it)),
                        "system"
                    )
                },
                safetySettings = generationConfig.safetySettings
            )
        )
    }
}