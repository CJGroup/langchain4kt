package chat.message

open class MessageType(private val typeName: String) {
    override fun toString() = typeName
}

sealed class BuiltinMessageType(typeName: String) : MessageType(typeName) {
    object Text : BuiltinMessageType("text")
    object Image : BuiltinMessageType("image")
    object Video : BuiltinMessageType("video")
    object Audio : BuiltinMessageType("audio")
    object FunctionCall : BuiltinMessageType("function_call")
}