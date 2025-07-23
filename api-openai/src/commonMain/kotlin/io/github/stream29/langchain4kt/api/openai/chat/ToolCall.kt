package io.github.stream29.proxy.relocate.com.aallam.openai.api.chat

import io.github.stream29.langchain4kt.api.openai.chat.FunctionCall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Details of the tool call.
 */
@Serializable
public sealed interface ToolCall {
}
@Serializable
@SerialName("function")
public data class Function(
    /** The ID of the tool call. **/
    @SerialName("id")
    public val id: String,
    /** The function that the model called. **/
    @SerialName("function")
    public val function: FunctionCall,
) : ToolCall