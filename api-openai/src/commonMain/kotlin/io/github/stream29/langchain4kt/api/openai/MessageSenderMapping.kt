package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.core.Role
import io.github.stream29.langchain4kt.core.message.MessageSender

internal fun MessageSender.toOpenAiRole(): Role =
    when(this) {
        MessageSender.User -> Role.User
        MessageSender.Model -> Role.Assistant
    }