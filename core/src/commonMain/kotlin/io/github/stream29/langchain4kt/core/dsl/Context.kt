package io.github.stream29.langchain4kt.core.dsl

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender

@LangchainDsl
fun Context.add(block: ContextBuilder.() -> Unit) {
    ContextBuilder(this).apply(block)
}

@LangchainDsl
fun Context.Companion.of(block: ContextBuilder.() -> Unit) =
    Context().also { ContextBuilder(it).apply(block) }

class ContextBuilder(
    private val context: Context
) {
    fun systemInstruction(text: String?) {
        context.systemInstruction = text
    }

    fun MessageSender.chat(text: String) {
        context.history.add(
            Message(
                sender = this,
                content = text
            )
        )
    }
}