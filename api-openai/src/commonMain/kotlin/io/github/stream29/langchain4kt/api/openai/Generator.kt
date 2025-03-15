package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequestBuilder
import com.aallam.openai.api.embedding.EmbeddingRequestBuilder
import com.aallam.openai.client.OpenAI
import io.github.stream29.langchain4kt.core.ConfigurableGenerator
import io.github.stream29.langchain4kt.core.ConfiguredGenerator
import io.github.stream29.langchain4kt.core.mapInputHistory
import io.github.stream29.langchain4kt.core.mapOutput

public fun OpenAI.asGenerator() =
    ConfigurableGenerator(::ChatCompletionRequestBuilder) { chatCompletion(it.build()) }

public fun OpenAI.asStreamingGenerator() =
    ConfigurableGenerator(::ChatCompletionRequestBuilder) { chatCompletions(it.build()) }

public fun OpenAI.asEmbeddingGenerator() =
    ConfigurableGenerator(::EmbeddingRequestBuilder) { embeddings(it.build()) }

public fun ConfiguredGenerator<ChatCompletionRequestBuilder, ChatCompletion>.mapUnion() =
    generateByMessages()
        .mapInputHistory { it: OpenAiHistoryMessageUnion -> it.asChatMessage() }
        .mapOutput { it.asUnionOfMessage() }