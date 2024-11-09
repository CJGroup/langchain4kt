package io.github.stream29.langchain4kt.api.googlegemini

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GeminiRequest(
    val contents: MutableList<GeminiContent>,
    val generationConfig: GeminiGenerationConfig = GeminiGenerationConfig(),
    val systemInstruction: GeminiContent? = null,
    val safetySettings: List<GeminiSafetySetting>? = null,
)

@Serializable
public data class GeminiSafetySetting(
    val category: GeminiHarmCategory,
    val threshold: GeminiHarmBlockThreshold,
)

@Serializable
public data class GeminiGenerationConfig(
    val stopSequences: List<String>? = null,
    val temperature: Double = 1.0,
    val topK: Int = 64,
    val topP: Double = 0.95,
    val maxOutputTokens: Int = 8192,
    val responseMimeType: String = "text/plain"
)

@Serializable
public enum class GeminiHarmBlockThreshold {
    @SerialName("0")
    HARM_BLOCK_THRESHOLD_UNSPECIFIED,
    @SerialName("1")
    BLOCK_LOW_AND_ABOVE,
    @SerialName("2")
    BLOCK_MEDIUM_AND_ABOVE,
    @SerialName("3")
    BLOCK_ONLY_HIGH,
    @SerialName("4")
    BLOCK_NONE
}

@Serializable
public enum class GeminiHarmCategory {
    @SerialName("0")
    HARM_CATEGORY_UNSPECIFIED,

    @SerialName("7")
    HARM_CATEGORY_HARASSMENT,

    @SerialName("8")
    HARM_CATEGORY_HATE_SPEECH,

    @SerialName("9")
    HARM_CATEGORY_SEXUALLY_EXPLICIT,

    @SerialName("10")
    HARM_CATEGORY_DANGEROUS_CONTENT
}