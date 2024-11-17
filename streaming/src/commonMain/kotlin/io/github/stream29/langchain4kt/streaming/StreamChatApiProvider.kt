package io.github.stream29.langchain4kt.streaming

import io.github.stream29.langchain4kt.core.dsl.ContextBuilder
import io.github.stream29.langchain4kt.core.dsl.of
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.coroutines.flow.Flow

/**
 * An LLM api provider that generates a streaming text response based on the given context.
 *
 * It should be stateless and concurrency-safe.
 *
 * @param MetaInfo The type of the meta info that the api provider generates.
 */
public interface StreamChatApiProvider<MetaInfo> {
    /**
     * Generates a streaming text response with meta info based on the given context.
     */
    public suspend fun generate(context: Context): StreamResponse<MetaInfo>
}

/**
 * Generates a response based on the given context.
 */
public suspend fun <T> StreamChatApiProvider<T>.generateFrom(block: ContextBuilder.() -> Unit): StreamResponse<T> =
    generate(Context.of(block))

/**
 * Generates a text response based on the given user message.
 *
 * Providing a simple way to test an [StreamChatApiProvider].
 * Using a [StreamRespondent] is suggested instead for building applications.
 */
public suspend fun StreamChatApiProvider<*>.generateFrom(userMessage: String): Flow<String> =
    generateFrom { MessageSender.User.chat(userMessage) }.message