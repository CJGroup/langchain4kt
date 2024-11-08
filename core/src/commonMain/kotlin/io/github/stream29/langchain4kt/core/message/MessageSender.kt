package io.github.stream29.langchain4kt.core.message

public sealed interface MessageSender {
    public data object User : MessageSender
    public data object Model : MessageSender
}