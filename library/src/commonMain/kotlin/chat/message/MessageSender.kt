package chat.message

sealed class MessageSender(private val name: String) {
    object User : MessageSender("user")
    object System : MessageSender("system")
    object Model : MessageSender("model")

    override fun toString() = name
}