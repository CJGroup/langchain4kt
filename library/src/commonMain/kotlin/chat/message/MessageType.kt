package chat.message

interface MessageType

sealed interface BuiltinMessageType : MessageType {
    data object Text : BuiltinMessageType
    data object Image : BuiltinMessageType
    data object Video : BuiltinMessageType
    data object Audio : BuiltinMessageType
    data object FunctionCall : BuiltinMessageType
}