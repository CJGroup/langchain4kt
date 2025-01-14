package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.langchain4kt.embedding.EmbeddingApiProvider
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse

/**
 * A [ChatApiProvider] that uses a [ApiProviderScheduler] to generate response.
 * @property scheduler [ApiProviderScheduler] to use
 */
public class ScheduledChatApiProvider<MetaInfo>(
    public val scheduler: ApiProviderScheduler<ChatApiProvider<MetaInfo>>,
) : ChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): Response<MetaInfo> {
        return scheduler.provide { it.generate(context) }
    }
}

/**
 * A [StreamChatApiProvider] that uses a [ApiProviderScheduler] to generate response.
 * @property scheduler [ApiProviderScheduler] to use
 */
public class ScheduledStreamChatApiProvider<MetaInfo>(
    public val scheduler: ApiProviderScheduler<StreamChatApiProvider<MetaInfo>>,
) : StreamChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): StreamResponse<MetaInfo> {
        return scheduler.provide { it.generate(context) }
    }
}

/**
 * A [EmbeddingApiProvider] that uses a [ApiProviderScheduler] to generate response.
 * @property scheduler [EmbeddingApiProvider] to use
 */
public class ScheduledEmbeddingChatApiProvider<T>(
    public val scheduler: ApiProviderScheduler<EmbeddingApiProvider<T>>,
) : EmbeddingApiProvider<T> {
    override suspend fun embed(text: String): T {
        return scheduler.provide { it.embed(text) }
    }
}