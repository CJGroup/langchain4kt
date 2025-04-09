package io.github.stream29.langchain4kt2.api.langchain4j

import kotlinx.serialization.Serializable

@Serializable
public data class Langchain4jToolCallRequest(
    val id: String,
    val toolId: String,
    val param: String
)

@Serializable
public data class Langchain4jToolCallRequestListMessage(
    val list: List<Langchain4jToolCallRequest>
)

@Serializable
public data class Langchain4jToolCallResultMessage(
    val id: String,
    val toolId: String,
    val result: String
)