package io.github.stream29.langchain4kt.core.message

data class Message<Content>(
    val type: MessageType,
    val sender: MessageSender,
    val content: Content
) {
    inline fun <reified T> asMessage() =
        if (content is T) this
        else throw ClassCastException("Cannot cast content to ${T::class.simpleName}")

    inline fun <reified T> safeAsMessage() =
        if (content is T) this
        else null
}

@Suppress("FunctionName")
fun TextMessage(
    sender: MessageSender,
    content: String
) = Message(MessageType.Text, sender, content)