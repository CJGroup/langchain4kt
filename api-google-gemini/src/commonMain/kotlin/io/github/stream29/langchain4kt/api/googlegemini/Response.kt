package io.github.stream29.langchain4kt.api.googlegemini

import kotlinx.serialization.Serializable

@Serializable
data class GeminiResponse(
    val candidates: List<Candidate>,
    val usageMetadata: UsageMetadata,
)

@Serializable
data class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int,
)

@Serializable
data class Candidate(
    val content: GeminiContent,
    val finishReason: String,
    val index: Int,
    val safetyRatings: List<SafetyRating>,
)

@Serializable
data class GeminiContent(
    val parts: List<MutableMap<String, String>>,
    val role: String
)

var GeminiContent.text: String
    get() = parts[0]["text"]!!
    set(value) {
        parts[0]["text"] = value
    }

@Serializable
data class SafetyRating(
    val category: String,
    val probability: String
)