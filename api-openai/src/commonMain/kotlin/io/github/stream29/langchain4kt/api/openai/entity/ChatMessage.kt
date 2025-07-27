package io.github.stream29.langchain4kt.api.openai.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@Serializable
public data class ChatMessage(
    public val role: String,
    public val content: Content? = null,
    public val name: String? = null,
    @Deprecated(message = "Deprecated in favor of toolCalls")
    @SerialName("function_call")
    public val functionCall: FunctionCall? = null,
    @SerialName("tool_calls")
    public val toolCalls: List<ToolCall>? = null,
    @SerialName("tool_call_id")
    public val toolCallId: String? = null,
)

@Serializable(with = ContentSerializer::class)
public sealed interface Content

@JvmInline
@Serializable
public value class TextContent(public val content: String) : Content

@JvmInline
@Serializable
public value class ListContent(public val content: List<ContentPart>) : Content

@Serializable
public sealed interface ContentPart

@Serializable
@SerialName("text")
public data class TextPart(public val text: String) : ContentPart

@Serializable
public sealed interface ToolCall

@Serializable
@SerialName("function")
public data class Function(
    public val function: FunctionCall,
) : ToolCall

@Serializable
public data class FunctionCall(
    public val name: String? = null,
    public val arguments: String? = null,
)
