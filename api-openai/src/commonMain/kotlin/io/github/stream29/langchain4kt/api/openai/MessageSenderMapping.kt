package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender

internal fun MessageSender.toOpenAiRole(): Role =
    when (this) {
        MessageSender.User -> Role.User
        MessageSender.Model -> Role.Assistant
    }

internal fun Context.toOpenAiMessageList(): List<ChatMessage> =
    buildList {
        systemInstruction?.let {
            add(
                ChatMessage(
                    role = Role.System,
                    content = it
                )
            )
        }
        history.forEach {
            add(
                ChatMessage(
                    role = it.sender.toOpenAiRole(),
                    content = it.content
                )
            )
        }
    }