package chat.message

sealed interface MessageSender {
    data object User : MessageSender
    data object System : MessageSender
    data object Model : MessageSender
}