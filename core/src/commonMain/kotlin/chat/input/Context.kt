package chat.input

import chat.message.Message

data class Context(
    var systemInstruction: Message<String>? = null,
    val history: MutableList<Message<*>> = mutableListOf()
)