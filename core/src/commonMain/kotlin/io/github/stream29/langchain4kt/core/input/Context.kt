package io.github.stream29.langchain4kt.core.input

import io.github.stream29.langchain4kt.core.message.Message

data class Context(
    var systemInstruction: Message<String>? = null,
    val history: MutableList<Message<*>> = mutableListOf()
)