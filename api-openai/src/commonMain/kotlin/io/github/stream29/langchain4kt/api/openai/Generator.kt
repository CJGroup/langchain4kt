package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.chat.ChatCompletionRequestBuilder
import com.aallam.openai.api.embedding.EmbeddingRequestBuilder
import com.aallam.openai.client.OpenAI
import io.github.stream29.langchain4kt.core.ConfigurableGenerator

public fun OpenAI.asGenerator() =
    ConfigurableGenerator(::ChatCompletionRequestBuilder) { chatCompletion(it.build()) }

public fun OpenAI.asStreamingGenerator() =
    ConfigurableGenerator(::ChatCompletionRequestBuilder) { chatCompletions(it.build()) }

public fun OpenAI.asEmbeddingGenerator() =
    ConfigurableGenerator(::EmbeddingRequestBuilder) { embeddings(it.build()) }