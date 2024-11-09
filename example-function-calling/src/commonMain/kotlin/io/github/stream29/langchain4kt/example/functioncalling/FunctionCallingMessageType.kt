package io.github.stream29.langchain4kt.example.functioncalling

data class FunctionCallingMessage(
    val type: FunctionCallingMessageType,
    val content: String
)

sealed interface FunctionCallingMessageType {
    data object UserMessage: FunctionCallingMessageType
    data object ModelMessage: FunctionCallingMessageType
    data object FunctionCall: FunctionCallingMessageType
    data object FunctionReturn: FunctionCallingMessageType
}