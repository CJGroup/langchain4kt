package io.github.stream29.langchain4kt2.api.openai

public data class OpenAiToolCallRequest(
    val id: String,
    val name: String,
    val param: String,
)

public data class OpenAiToolCallRequestListMessage(
    public val list: List<OpenAiToolCallRequest>
)

public data class OpenAiToolCallResultMessage(
    val id: String,
    val result: String,
)