package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.core.Generator
import org.springframework.ai.embedding.EmbeddingModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public fun EmbeddingModel.asEmbeddingGenerator(): Generator<String, FloatArray> = { text ->
    suspendCoroutine { continuation ->
        continuation.resume(embed(text))
    }
}