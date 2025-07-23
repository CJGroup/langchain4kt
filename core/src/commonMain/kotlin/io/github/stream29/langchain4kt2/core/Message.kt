package io.github.stream29.langchain4kt2.core

import kotlinx.serialization.Serializable

@Serializable
public enum class MessageDirection {
    Input,
    Output
}

public interface Message {
    public val direction: MessageDirection
}

public interface MultipartMessage<out T : Message> : Message, List<T>

public interface TextMessage : Message {
    public val text: String
}

public interface ReasoningMessage : Message {
    public val reasoning: String
}

public interface UrlImageMessage : Message {
    public val url: String
}