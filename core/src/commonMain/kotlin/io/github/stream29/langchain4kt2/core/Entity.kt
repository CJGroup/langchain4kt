package io.github.stream29.langchain4kt2.core

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

public interface Message {
    public val direction: DataDirection
}

public abstract class InputMessage : Message {
    public override val direction: DataDirection
        get() = DataDirection.Input
}

public abstract class OutputMessage : Message {
    public override val direction: DataDirection
        get() = DataDirection.Output
}

public data class ListInputMessage<out T>(
    val list: List<T>
) : InputMessage()

public data class ListOutputMessage<out T>(
    val list: List<T>
) : OutputMessage()

public interface TextMessage : Message {
    public val text: String
}

public interface UrlImageMessage : Message {
    public val url: String
}

@Serializable
public data class SystemTextMessage(
    public override val text: String
) : InputMessage(), TextMessage

@Serializable
public data class UserTextMessage(
    public override val text: String
) : InputMessage(), TextMessage

@Serializable
public data class UserUrlImageMessage(
    public override val url: String
) : InputMessage(), UrlImageMessage

@Serializable
public data class ModelTextMessage(
    public override val text: String
) : OutputMessage(), TextMessage

@Serializable
public data class ModelUrlImageMessage(
    public override val url: String
) : OutputMessage(), UrlImageMessage

@Serializable
public data class ToolCallRequestMessage(
    val toolId: String,
    val param: String
) : OutputMessage()

@Serializable
public data class ToolCallRequestListMessage(
    val list: List<ToolCallRequestMessage>
) : OutputMessage()

@Serializable
public data class ToolCallResultMessage(
    val toolId: String,
    val param: String,
    val result: String
) : InputMessage()

@Serializable
public data class ToolCallResultListMessage(
    val list: List<ToolCallResultMessage>
): InputMessage()