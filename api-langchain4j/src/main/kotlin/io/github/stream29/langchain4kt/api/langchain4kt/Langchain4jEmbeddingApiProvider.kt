package io.github.stream29.langchain4kt.api.langchain4kt

import dev.langchain4j.model.embedding.EmbeddingModel
import io.github.stream29.langchain4kt.embedding.EmbeddingApiProvider

public data class Langchain4jEmbeddingApiProvider(
    val model: EmbeddingModel
): EmbeddingApiProvider<FloatArray> {
    override suspend fun embed(text: String): FloatArray {
        return model.embed(text).content().vector()
    }
}