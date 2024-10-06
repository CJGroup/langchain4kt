package chat.message

open class MessageSender(private val name: String) {
    override fun toString() = name
}

sealed class BuiltinMessageSender(name: String) : MessageSender(name) {
    object User : BuiltinMessageSender("user")
    object System : BuiltinMessageSender("system")
    object Model : BuiltinMessageSender("model")
}