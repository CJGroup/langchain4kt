package io.github.stream29.langchain4kt.core.input

import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.ChatModel
import kotlinx.serialization.Serializable

/**
 * Context of the conversation with [ChatModel].
 */
@Serializable
public data class Context(
    var systemInstruction: String? = null,
    val history: MutableList<Message> = mutableListOf()
) {
    public companion object
}