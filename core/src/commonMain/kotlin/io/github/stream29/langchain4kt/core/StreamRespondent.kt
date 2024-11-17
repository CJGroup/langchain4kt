package io.github.stream29.langchain4kt.core

import kotlinx.coroutines.flow.Flow

public interface StreamRespondent {
    public suspend fun chat(message: String): Flow<String>
}