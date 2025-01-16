package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.ChatModel
import io.github.stream29.langchain4kt.core.Respondent
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.langchain4kt.embedding.EmbeddingApiProvider
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamChatModel
import io.github.stream29.langchain4kt.streaming.StreamRespondent
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.flow.Flow

public typealias ChatApiProviderFunc<MetaInfo> = Generator<Context, Response<MetaInfo>>
public typealias RespondentFunc = Generator<String, String>
public typealias ChatModelFunc = Generator<String, String>
public typealias EmbeddingApiProviderFunc<T> = Generator<String, T>
public typealias StreamChatApiProviderFunc<MetaInfo> = Generator<Context, StreamResponse<MetaInfo>>
public typealias StreamRespondentFunc = Generator<String, Flow<String>>
public typealias StreamChatModelFunc = Generator<String, Flow<String>>

/**
 * A [ChatApiProvider] that uses a plugin to modify the behavior of the provider.
 * @property baseChatApiProvider [ChatApiProvider] to use
 * @property plugin Plugin to use
 */
public class PluginChatApiProvider<MetaInfo>(
    public val baseChatApiProvider: ChatApiProvider<MetaInfo>,
    public val plugin: Plugin<ChatApiProviderFunc<MetaInfo>>,
) : ChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): Response<MetaInfo> {
        return plugin(baseChatApiProvider::generate)(context)
    }
}

public class PluginRespondent(
    public val baseRespondent: Respondent,
    public val aroundChat: Plugin<RespondentFunc>,
) : Respondent {
    override suspend fun chat(message: String): String {
        return aroundChat(baseRespondent::chat)(message)
    }
}

public class PluginChatModel(
    public val baseChatModel: ChatModel,
    public val aroundChat: Plugin<ChatModelFunc>
) : ChatModel {
    public override val context: Context by baseChatModel::context
    override suspend fun chat(message: String): String {
        return aroundChat(baseChatModel::chat)(message)
    }
}

public class PluginEmbeddingApiProvider<T>(
    public val baseEmbeddingApiProvider: EmbeddingApiProvider<T>,
    public val aroundEmbed: Plugin<EmbeddingApiProviderFunc<T>>,
) : EmbeddingApiProvider<T> {
    override suspend fun embed(text: String): T {
        return aroundEmbed(baseEmbeddingApiProvider::embed)(text)
    }
}

public class PluginStreamChatApiProvider<MetaInfo>(
    public val baseStreamChatApiProvider: StreamChatApiProvider<MetaInfo>,
    public val aroundGenerate: Plugin<StreamChatApiProviderFunc<MetaInfo>>,
) : StreamChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): StreamResponse<MetaInfo> {
        return aroundGenerate(baseStreamChatApiProvider::generate)(context)
    }
}

public class PluginStreamRespondent(
    public val baseStreamRespondent: StreamRespondent,
    public val aroundChat: Plugin<StreamRespondentFunc>
) : StreamRespondent {
    override suspend fun chat(message: String): Flow<String> {
        return aroundChat(baseStreamRespondent::chat)(message)
    }
}

public class PluginStreamChatModel(
    public val baseStreamChatModel: StreamChatModel,
    public val aroundChat: Plugin<StreamChatModelFunc>
) : StreamChatModel {
    public override val context: Context by baseStreamChatModel::context
    public override val isReady: Boolean by baseStreamChatModel::isReady
    override suspend fun chat(message: String): Flow<String> {
        return aroundChat(baseStreamChatModel::chat)(message)
    }
}

/**
 * Install a list of [Plugin] to a [ChatApiProvider].
 * @param plugins List of plugins to install
 */
public fun <MetaInfo> ChatApiProvider<MetaInfo>.install(vararg plugins: Plugin<ChatApiProviderFunc<MetaInfo>>): ChatApiProvider<MetaInfo> =
    plugins.fold(this) { chatApiProvider, plugin ->
        PluginChatApiProvider(
            chatApiProvider,
            plugin
        )
    }

/**
 * Install a list of [Plugin] to a [Respondent].
 * @param plugins List of plugins to install
 */
public fun Respondent.install(vararg plugins: Plugin<RespondentFunc>): Respondent =
    plugins.fold(this) { respondent, plugin ->
        PluginRespondent(
            respondent,
            plugin
        )
    }

public fun ChatModel.install(vararg plugins: Plugin<ChatModelFunc>): ChatModel =
    plugins.fold(this) { chatModel, plugin ->
        PluginChatModel(
            chatModel,
            plugin
        )
    }

public fun <T> EmbeddingApiProvider<T>.install(vararg plugins: Plugin<EmbeddingApiProviderFunc<T>>): EmbeddingApiProvider<T> =
    plugins.fold(this) { embeddingApiProvider, plugin ->
        PluginEmbeddingApiProvider(
            embeddingApiProvider,
            plugin
        )
    }

/**
 * Install a list of [Plugin] to a [StreamChatApiProvider].
 * The behaviour of plugin is just like that on [ChatApiProvider], but waiting for the stream generation complete.
 * @param plugins List of plugins to install
 */
public fun <T> StreamChatApiProvider<T>.install(vararg plugins: Plugin<StreamChatApiProviderFunc<T>>): StreamChatApiProvider<T> =
    plugins.fold(this) { chatApiProvider, plugin ->
        PluginStreamChatApiProvider(
            chatApiProvider,
            plugin
        )
    }

public fun StreamRespondent.install(vararg plugins: Plugin<StreamRespondentFunc>): StreamRespondent =
    plugins.fold(this) { respondent, plugin ->
        PluginStreamRespondent(
            respondent,
            plugin
        )
    }

public fun StreamChatModel.install(vararg plugins: Plugin<StreamChatModelFunc>): StreamChatModel =
    plugins.fold(this) { chatModel, plugin ->
        PluginStreamChatModel(
            chatModel,
            plugin
        )
    }