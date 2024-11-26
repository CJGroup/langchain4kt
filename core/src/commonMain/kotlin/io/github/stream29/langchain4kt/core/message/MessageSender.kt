package io.github.stream29.langchain4kt.core.message

import kotlinx.serialization.Serializable

/**
 * Sender of a [Message]
 */
@Serializable
public sealed interface MessageSender {
    @Serializable
    public data object User : MessageSender
    @Serializable
    public data object Model : MessageSender
}