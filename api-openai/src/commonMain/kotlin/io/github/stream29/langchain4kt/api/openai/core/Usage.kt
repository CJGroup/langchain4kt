package io.github.stream29.langchain4kt.api.openai.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Usage(
    /**
     * Count of prompts tokens.
     */
    @SerialName("prompt_tokens") val promptTokens: Int? = null,
    /**
     * Count of completion tokens.
     */
    @SerialName("completion_tokens") val completionTokens: Int? = null,
    /**
     * Count of total tokens.
     */
    @SerialName("total_tokens") val totalTokens: Int? = null,
)
