package io.github.stream29.langchain4kt.api.openai.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChatCompletionChunk(
    public val id: String? = null,
    public val created: Long,
    public val model: String,
    public val choices: List<ChatChunk>,
    public val usage: Usage? = null,
    @SerialName("system_fingerprint")
    public val systemFingerprint: String? = null,
)

@Serializable
public data class ChatChunk(
    public val index: Int,
    public val delta: ChatDelta? = null,
    @SerialName("finish_reason")
    public val finishReason: String? = null,
)

@Serializable
public data class ChatDelta(
    public val role: String? = null,
    public val content: String? = null,
    @SerialName("reasoning_content")
    public val reasoningContent: String? = null,
    @Deprecated(message = "Deprecated in favor of toolCalls")
    @SerialName("function_call") public val functionCall: FunctionCall? = null,
    @SerialName("tool_calls") public val toolCalls: List<ToolCallChunk>? = null,
    @SerialName("tool_call_id") public val toolCallId: String? = null,
)

@Serializable
public data class Usage(
    @SerialName("prompt_tokens")
    val promptTokens: Int? = null,
    @SerialName("completion_tokens")
    val completionTokens: Int? = null,
    @SerialName("total_tokens")
    val totalTokens: Int? = null,
)

@Serializable
public data class ToolCallChunk(
    public val index: Int,
    public val type: String? = null,
    public val id: String? = null,
    public val function: FunctionCall? = null,
)