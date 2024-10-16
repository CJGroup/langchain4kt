package chat.message

sealed interface MessageType {
    data object Text : MessageType
    data object Image : MessageType
    data object Video : MessageType
    data object Audio : MessageType
    data object FunctionCall : MessageType
    data object FunctionCallResult : MessageType
}