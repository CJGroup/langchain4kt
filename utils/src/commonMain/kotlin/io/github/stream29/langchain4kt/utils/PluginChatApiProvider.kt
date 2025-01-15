package io.github.stream29.langchain4kt.utils

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.Respondent
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.github.stream29.langchain4kt.streaming.StreamChatApiProvider
import io.github.stream29.langchain4kt.streaming.StreamResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

public typealias ChatApiProviderPlugin<T> = GeneratorPlugin<Context, Response<T>>
public typealias RespondentPlugin = GeneratorPlugin<String, String>
public typealias EmbeddingApiProviderPlugin<T> = GeneratorPlugin<String, T>

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

public class PluginStreamChatApiProvider<MetaInfo>(
    public val baseStreamChatApiProvider: StreamChatApiProvider<MetaInfo>,
    public val aroundGenerate: ChatApiProviderPlugin<MetaInfo>,
) : StreamChatApiProvider<MetaInfo> {
    override suspend fun generate(context: Context): StreamResponse<MetaInfo> {
        val streamResult = baseStreamChatApiProvider.generate(context)
        val buffer = mutableListOf<String>()
        val deferredResponse = CompletableDeferred<Response<MetaInfo>>()
        coroutineScope {
            launch {
                aroundGenerate(context) { deferredResponse.await() }
            }
        }
        streamResult.message.onEach {
            buffer.add(it)
        }.onCompletion {
            deferredResponse.complete(Response(buffer.joinToString(""), streamResult.metaInfo.await()))
        }
        return streamResult
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
 * Install a list of [ChatApiProviderPlugin] to a [StreamChatApiProvider].
 * The behaviour of plugin is just like that on [ChatApiProvider], but waiting for the stream generation complete.
 * @param plugins List of plugins to install
 */
public fun <T> StreamChatApiProvider<T>.install(vararg plugins: ChatApiProviderPlugin<T>): StreamChatApiProvider<T> =
    plugins.fold(this) { chatApiProvider, plugin ->
        PluginStreamChatApiProvider(
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
