package io.github.stream29.langchain4kt.core.message

/**
 * Message sent by [sender]
 */
public data class Message(
    val sender: MessageSender,
    val content: String
)