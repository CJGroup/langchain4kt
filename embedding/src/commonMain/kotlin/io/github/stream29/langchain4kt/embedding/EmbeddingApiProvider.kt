package io.github.stream29.langchain4kt.embedding

public interface EmbeddingApiProvider<T> {
    public suspend fun embed(text: String): T
}

public data class WrappedEmbeddingApiProvider<T, V>(
    val apiProvider: EmbeddingApiProvider<T>,
    val wrapper: (T) -> V
) : EmbeddingApiProvider<V> {
    override suspend fun embed(text: String): V = wrapper(apiProvider.embed(text))
}