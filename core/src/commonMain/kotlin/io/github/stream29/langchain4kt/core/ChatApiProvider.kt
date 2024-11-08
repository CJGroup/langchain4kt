package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.Response

public interface ChatApiProvider<MetaInfo> {
    public suspend fun generate(context: Context): Response<MetaInfo>
}

public suspend fun <T> ChatApiProvider<T>.generateFrom(block: ContextBuilder.() -> Unit): Response<T> =
    generate(Context.of(block))

public suspend fun ChatApiProvider<*>.generateFrom(userMessage: String): String =
    generateFrom { MessageSender.User.chat(userMessage) }.message