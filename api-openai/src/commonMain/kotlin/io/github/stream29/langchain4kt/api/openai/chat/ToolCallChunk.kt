package io.github.stream29.langchain4kt.api.openai.chat

import kotlinx.serialization.Serializable

/**
 * Details of the tool call chunk.
 */
@Serializable
public data class ToolCallChunk(
    /** Tool call index. Required in the case of chat stream variant **/
    public val index: Int,
    /** The type of the tool call. **/
    public val type: String? = null,
    /** The ID of the tool call. **/
    public val id: String? = null,
    /** The function that the model called. **/
    public val function: FunctionCall? = null,
)
