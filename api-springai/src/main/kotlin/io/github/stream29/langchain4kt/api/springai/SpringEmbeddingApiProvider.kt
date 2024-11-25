package io.github.stream29.langchain4kt.api.springai

import io.github.stream29.langchain4kt.embedding.EmbeddingApiProvider
import org.springframework.ai.embedding.EmbeddingModel

/**
 * Wrapping [EmbeddingModel] to [EmbeddingApiProvider].
 */
public data class SpringEmbeddingApiProvider(
    val model: EmbeddingModel
) : EmbeddingApiProvider<FloatArray> {
    override suspend fun embed(text: String): FloatArray {
        return model.embed(text)
    }
}

/**
 * Wrapping [EmbeddingModel] to [EmbeddingApiProvider].
 */
public fun EmbeddingModel.asLangchain4ktProvider(): SpringEmbeddingApiProvider = SpringEmbeddingApiProvider(this)