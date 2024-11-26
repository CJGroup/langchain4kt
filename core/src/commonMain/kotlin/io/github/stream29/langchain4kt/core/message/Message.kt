package io.github.stream29.langchain4kt.core.message

import kotlinx.serialization.Serializable

/**
 * Message sent by [sender]
 */
@Serializable
public data class Message(
    val sender: MessageSender,
    val content: String
)