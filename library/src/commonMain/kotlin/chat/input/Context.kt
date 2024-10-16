package chat.input

import chat.message.Message

data class Context(
    val config: ChatConfig = ChatConfig(),
    var systemInstruction: Message<String>? = null,
    val history: MutableList<Message<*>> = mutableListOf()
)