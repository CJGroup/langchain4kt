package chat.message

interface Message {
    val type: MessageType
    val sender: MessageSender
    val content: Any
}

class TextMessage(
    override val sender: MessageSender,
    override val content: String
) : Message {
    override val type = BuiltinMessageType.Text
}