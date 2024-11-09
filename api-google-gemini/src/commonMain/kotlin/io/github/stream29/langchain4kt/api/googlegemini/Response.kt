package io.github.stream29.langchain4kt.api.googlegemini

import kotlinx.serialization.Serializable

/**
 * Raw response from Google Gemini API.
 */
@Serializable
public data class GeminiResponse(
    val candidates: List<GeminiCandidate>,
    val usageMetadata: GeminiUsageMetadata,
)

/**
 * Metadata of the usage of tokens in the response.
 */
@Serializable
public data class GeminiUsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int,
)

/**
 * Candidate of the response.
 */
@Serializable
public data class GeminiCandidate(
    val content: GeminiContent,
    val finishReason: String,
    val index: Int? = null,
    val safetyRatings: List<GeminiSafetyRating>? = null,
)

/**
 * Content of the candidate.
 */
@Serializable
public data class GeminiContent(
    val parts: List<MutableMap<String, String>>,
    val role: String
)

/**
 * Safety rating of the candidate.
 */
@Serializable
public data class GeminiSafetyRating(
    val category: String,
    val probability: String
)

internal var GeminiContent.text: String
    get() = parts[0]["text"]!!
    set(value) {
        parts[0]["text"] = value
    }
