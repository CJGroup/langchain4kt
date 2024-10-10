package chat.input

import chat.message.Message

interface IContext {
    val config: IChatConfig
    var systemInstruction: Message?
    val history: MutableList<Message>
}

data class Context(
    override val config: ChatConfig = ChatConfig(),
    override var systemInstruction: Message? = null,
    override val history: MutableList<Message> = mutableListOf()
) : IContext