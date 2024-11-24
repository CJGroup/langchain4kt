package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage

internal fun fromContext(context: Context) = buildList {
    context.systemInstruction?.let { add(SystemMessage(it)) }
    context.history.forEach {
        val messageTransformer: (String) -> Message = when (it.sender) {
            MessageSender.User -> ::UserMessage
            MessageSender.Model -> ::AssistantMessage
        }
        add(messageTransformer(it.content))
    }
}
