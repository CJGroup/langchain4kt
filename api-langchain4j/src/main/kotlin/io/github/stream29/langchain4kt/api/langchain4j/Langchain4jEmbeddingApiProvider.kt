package io.github.stream29.langchain4kt.api.langchain4j

import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import io.github.stream29.langchain4kt.core.Generator
import io.github.stream29.langchain4kt.core.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

public fun EmbeddingModel.toGenerator(): Generator<List<TextSegment>, Response<List<Embedding>, Map<String, Any>>> =
    { segments ->
        suspendCoroutine { continuation ->
            val rawResponse = this.embedAll(segments)
            continuation.resume(
                Response(
                    rawResponse.content(),
                    rawResponse.metadata()
                )
            )
        }
    }