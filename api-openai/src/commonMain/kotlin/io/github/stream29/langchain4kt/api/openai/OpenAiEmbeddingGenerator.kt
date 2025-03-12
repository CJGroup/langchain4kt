package io.github.stream29.langchain4kt.api.openai

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.embedding.EmbeddingRequest
import com.aallam.openai.api.embedding.EmbeddingResponse
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.github.stream29.langchain4kt.core.Generator

public class OpenAiEmbeddingGenerator(
    public val clientConfig: OpenAIConfig,
    public val generationConfig: OpenAiGenerationConfig,
    public val requestOptions: RequestOptions = RequestOptions(),
) : Generator<List<String>, EmbeddingResponse> {
    public val client: OpenAI = OpenAI(clientConfig)
    override suspend fun invoke(p1: List<String>): EmbeddingResponse {
        return client.embeddings(
            EmbeddingRequest(
                model = ModelId(generationConfig.model),
                input = p1,
                user = generationConfig.user
            ),
            requestOptions
        )
    }
}