package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.output.Response

/**
 * An LLM api provider that generates a text response based on the given context.
 *
 * It should be stateless and thread-safe.
 *
 * @param MetaInfo The type of the meta info that the api provider generates.
 */
public interface ChatApiProvider<MetaInfo> {
    /**
     * Generates a text response with meta info based on the given context.
     */
    public suspend fun generate(context: Context): Response<MetaInfo>
}

/**
 * Generates a response based on the given context.
 */
public suspend fun <T> ChatApiProvider<T>.generateFrom(block: ContextBuilder.() -> Unit): Response<T> =
    generate(Context.of(block))

/**
 * Generates a text response based on the given user message.
 *
 * Providing a simple way to test an [ChatApiProvider].
 * Using a [Respondent] is suggested instead for building applications.
 */
public suspend fun ChatApiProvider<*>.generateFrom(userMessage: String): String =
    generateFrom { MessageSender.User.chat(userMessage) }.message