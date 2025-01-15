package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.langchain4kt.embedding.EmbeddingApiProvider
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse

/**
 * A [ChatApiProvider] that uses a [ApiProviderDispatcher] to generate response.
 * @property dispatcher [ApiProviderDispatcher] to use
 */
public class DispatchedChatApiProvider<MetaInfo>(
    public val dispatcher: ApiProviderDispatcher<ChatApiProvider<MetaInfo>>,
) : ChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): Response<MetaInfo> {
        return dispatcher.dispatch { it.generate(context) }
    }
}

/**
 * A [StreamChatApiProvider] that uses a [ApiProviderDispatcher] to generate response.
 * @property dispatcher [ApiProviderDispatcher] to use
 */
public class DispatchedStreamChatApiProvider<MetaInfo>(
    public val dispatcher: ApiProviderDispatcher<StreamChatApiProvider<MetaInfo>>,
) : StreamChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): StreamResponse<MetaInfo> {
        return dispatcher.dispatch { it.generate(context) }
    }
}

/**
 * A [EmbeddingApiProvider] that uses a [ApiProviderDispatcher] to generate response.
 * @property dispatcher [EmbeddingApiProvider] to use
 */
public class DispatchedEmbeddingChatApiProvider<T>(
    public val dispatcher: ApiProviderDispatcher<EmbeddingApiProvider<T>>,
) : EmbeddingApiProvider<T> {
    override suspend fun embed(text: String): T {
        return dispatcher.dispatch { it.embed(text) }
    }
}