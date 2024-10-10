package chat.output

import chat.message.Message

interface Response: Message {
    companion object {
        fun of(message: Message): Response = MessageResponse(message)
    }
}

data class MessageResponse(val message: Message) : Response {
    override val type by message::type
    override val sender by message::sender
    override val content by message::content
}

// TODO: Streaming response