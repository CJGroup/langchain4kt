package chat.input

import chat.message.Message

interface IContext {
    val config: IChatConfig
    val history: MutableList<Message>
}

class Context(
    override val config: ChatConfig = ChatConfig(),
    override val history: MutableList<Message> = mutableListOf()
) : IContext