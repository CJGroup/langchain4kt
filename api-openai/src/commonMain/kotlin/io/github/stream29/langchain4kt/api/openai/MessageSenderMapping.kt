package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender

internal fun MessageSender.toOpenAiRole(): Role =
    when(this) {
        MessageSender.User -> Role.User
        MessageSender.Model -> Role.Assistant
    }

internal fun Context.toOpenAiMessageList(): List<ChatMessage> {
    val messageList = mutableListOf<ChatMessage>()
    systemInstruction?.let {
        ChatMessage(
            role = Role.System,
            content = it
        )
    }?.let { messageList.add(it) }
    history.asSequence().map {
        ChatMessage(
            role = it.sender.toOpenAiRole(),
            content = it.content
        )
    }.forEach { messageList.add(it) }
    return messageList
}