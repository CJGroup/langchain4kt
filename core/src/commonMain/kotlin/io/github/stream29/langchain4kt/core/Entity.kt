package io.github.stream29.langchain4kt.core

import kotlinx.serialization.Serializable

@Serializable
public data class Response<out Content, out MetaInfo>(
    val content: Content,
    val metaInfo: MetaInfo
)

@Serializable
public enum class DataDirection {
    Input,
    Output
}

@Serializable
public data class ChatMessage<out Content>(
    val direction: DataDirection,
    val content: Content
)

@Serializable
public data class FunctionCallRequest<out Param>(
    val functionId: String,
    val param: Param
)

@Serializable
public data class FunctionCallResponse<out Param, out Result>(
    val functionId: String,
    val param: Param,
    val result: Result
)