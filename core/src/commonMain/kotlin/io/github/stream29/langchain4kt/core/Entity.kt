package io.github.stream29.langchain4kt.core

public data class Response<out Content, out MetaInfo>(
    val content: Content,
    val metaInfo: MetaInfo
)

public enum class DataDirection {
    Input,
    Output
}

public data class ChatMessage<Content>(
    val direction: DataDirection,
    val content: Content
)