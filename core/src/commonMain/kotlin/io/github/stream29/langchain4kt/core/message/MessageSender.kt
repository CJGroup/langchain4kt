package io.github.stream29.langchain4kt.core.message

sealed interface MessageSender {
    data object User : MessageSender
    data object Model : MessageSender
}