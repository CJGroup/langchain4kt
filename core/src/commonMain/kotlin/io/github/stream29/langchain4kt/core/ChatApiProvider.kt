package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.Response

interface ChatApiProvider<MetaInfo> {
    suspend fun generate(context: Context): Response<MetaInfo>
}

suspend fun <T> ChatApiProvider<T>.generateFrom(block: ContextBuilder.() -> Unit) =
    generate(Context.of(block))

suspend fun ChatApiProvider<*>.generate(userMessage: String) =
    generateFrom { MessageSender.User.chat(userMessage) }.message