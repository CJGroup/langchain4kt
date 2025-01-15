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

public typealias ChatApiProviderPlugin<T> = GeneratorPlugin<Context, Response<T>>
public typealias RespondentPlugin = GeneratorPlugin<String, String>
public typealias ChatModelPlugin = GeneratorPlugin<String, String>
public typealias EmbeddingApiProviderPlugin<T> = GeneratorPlugin<String, T>
public typealias StreamChatApiProviderPlugin<T> = GeneratorPlugin<Context, StreamResponse<T>>
public typealias StreamRespondentPlugin = GeneratorPlugin<String, Flow<String>>
public typealias StreamChatModelPlugin = GeneratorPlugin<String, Flow<String>>

/**
 * A [ChatApiProvider] that uses a plugin to modify the behavior of the provider.
 * @property baseChatApiProvider [ChatApiProvider] to use
 * @property aroundGenerate Plugin to use
 */
public class PluginChatApiProvider<MetaInfo>(
    public val baseChatApiProvider: ChatApiProvider<MetaInfo>,
    public val aroundGenerate: ChatApiProviderPlugin<MetaInfo>,
) : ChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): Response<MetaInfo> {
        return aroundGenerate(context, baseChatApiProvider::generate)
    }
}

public class PluginRespondent(
    public val baseRespondent: Respondent,
    public val aroundChat: RespondentPlugin,
) : Respondent {
    override suspend fun chat(message: String): String {
        return aroundChat(message, baseRespondent::chat)
    }
}

public class PluginChatModel(
    public val baseChatModel: ChatModel,
    public val aroundChat: ChatModelPlugin
) : ChatModel {
    public override val context: Context by baseChatModel::context
    override suspend fun chat(message: String): String {
        return aroundChat(message, baseChatModel::chat)
    }
}

public class PluginEmbeddingApiProvider<T>(
    public val baseEmbeddingApiProvider: EmbeddingApiProvider<T>,
    public val aroundEmbed: EmbeddingApiProviderPlugin<T>,
) : EmbeddingApiProvider<T> {
    override suspend fun embed(text: String): T {
        return aroundEmbed(text, baseEmbeddingApiProvider::embed)
    }
}

public class PluginStreamChatApiProvider<MetaInfo>(
    public val baseStreamChatApiProvider: StreamChatApiProvider<MetaInfo>,
    public val aroundGenerate: StreamChatApiProviderPlugin<MetaInfo>,
) : StreamChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): StreamResponse<MetaInfo> {
        return aroundGenerate(context, baseStreamChatApiProvider::generate)
    }
}

public class PluginStreamRespondent(
    public val baseStreamRespondent: StreamRespondent,
    public val aroundChat: StreamRespondentPlugin
) : StreamRespondent {
    override suspend fun chat(message: String): Flow<String> {
        return aroundChat(message, baseStreamRespondent::chat)
    }
}

public class PluginStreamChatModel(
    public val baseStreamChatModel: StreamChatModel,
    public val aroundChat: StreamChatModelPlugin
) : StreamChatModel {
    public override val context: Context by baseStreamChatModel::context
    public override val isReady: Boolean by baseStreamChatModel::isReady
    override suspend fun chat(message: String): Flow<String> {
        return aroundChat(message, baseStreamChatModel::chat)
    }
}

/**
 * Install a list of [ChatApiProviderPlugin] to a [ChatApiProvider].
 * @param plugins List of plugins to install
 */
public fun <T> ChatApiProvider<T>.install(vararg plugins: ChatApiProviderPlugin<T>): ChatApiProvider<T> =
    plugins.fold(this) { chatApiProvider, plugin ->
        PluginChatApiProvider(
            chatApiProvider,
            plugin
        )
    }

/**
 * Install a list of [RespondentPlugin] to a [Respondent].
 * @param plugins List of plugins to install
 */
public fun Respondent.install(vararg plugins: RespondentPlugin): Respondent =
    plugins.fold(this) { respondent, plugin ->
        PluginRespondent(
            respondent,
            plugin
        )
    }

public fun ChatModel.install(vararg plugins: ChatModelPlugin): ChatModel =
    plugins.fold(this) { chatModel, plugin ->
        PluginChatModel(
            chatModel,
            plugin
        )
    }

public fun <T> EmbeddingApiProvider<T>.install(vararg plugins: EmbeddingApiProviderPlugin<T>): EmbeddingApiProvider<T> =
    plugins.fold(this) { embeddingApiProvider, plugin ->
        PluginEmbeddingApiProvider(
            embeddingApiProvider,
            plugin
        )
    }

/**
 * Install a list of [ChatApiProviderPlugin] to a [StreamChatApiProvider].
 * The behaviour of plugin is just like that on [ChatApiProvider], but waiting for the stream generation complete.
 * @param plugins List of plugins to install
 */
public fun <T> StreamChatApiProvider<T>.install(vararg plugins: StreamChatApiProviderPlugin<T>): StreamChatApiProvider<T> =
    plugins.fold(this) { chatApiProvider, plugin ->
        PluginStreamChatApiProvider(
            chatApiProvider,
            plugin
        )
    }

public fun StreamRespondent.install(vararg plugins: StreamRespondentPlugin): StreamRespondent =
    plugins.fold(this) { respondent, plugin ->
        PluginStreamRespondent(
            respondent,
            plugin
        )
    }

public fun StreamChatModel.install(vararg plugins: StreamChatModelPlugin): StreamChatModel =
    plugins.fold(this) { chatModel, plugin ->
        PluginStreamChatModel(
            chatModel,
            plugin
        )
    }