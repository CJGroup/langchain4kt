package io.github.stream29.langchain4kt.core.message

sealed interface MessageType<Content> {
    data object Text : MessageType<String>
    data object Image : MessageType<Nothing>
    data object Video : MessageType<Nothing>
    data object Audio : MessageType<Nothing>
    data object FunctionCall : MessageType<Nothing>
    data object FunctionCallResult : MessageType<Nothing>
}