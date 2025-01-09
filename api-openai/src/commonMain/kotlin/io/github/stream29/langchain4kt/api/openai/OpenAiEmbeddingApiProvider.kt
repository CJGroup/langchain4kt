package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.github.stream29.langchain4kt.embedding.EmbeddingApiProvider

public class OpenAiEmbeddingApiProvider(
    public val clientConfig: OpenAIConfig,
    public val generationConfig: OpenAiGenerationConfig,
    public val requestOptions: RequestOptions = RequestOptions(),
) : EmbeddingApiProvider<List<Double>> {
    public val client: OpenAI = OpenAI(clientConfig)
    override suspend fun embed(text: String): List<Double> {
        return client.embeddings(
            EmbeddingRequest(
                model = ModelId(generationConfig.model),
                input = listOf(text),
                user = generationConfig.user
            ),
            requestOptions
        ).embeddings.firstOrNull()?.embedding
            ?: throw IllegalStateException("No legal response message found")
    }
}