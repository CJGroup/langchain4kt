package io.github.stream29.langchain4kt.api.baiduqianfan

import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.serialization.Serializable

//"messages": [
//{"role":"user", "content": "给我推荐一些自驾游路线"}
//],
//"stream": true

@Serializable
data class QianfanChatRequest(
    val messages: List<QianfanMessage>,
    val temperature: Float = 0.8f,
    val topP: Float = 0.8f,
    val penaltyScore: Float = 1.0f,
    val stream: Boolean = false,
    val enableSystemMemory: Boolean = false,
    val systemMemoryId: String? = null,
    val system: String? = null,
    val stop: List<String>? = null,
    val disableSearch: Boolean = false,
    val enableCitation: Boolean = false,
    val enableTrace: Boolean = false,
    val maxOutputTokens: Int = 1024,
    val responseFormat: String = "text",
    val userIp: String? = null,
    val userId: String? = null,
)

@Serializable
data class GenerateConfig(
    val temperature: Float = 0.8f,
    val topP: Float = 0.8f,
    val penaltyScore: Float = 1.0f,
    val stream: Boolean = false,
    val enableSystemMemory: Boolean = false,
    val systemMemoryId: String? = null,
    val system: String? = null,
    val stop: List<String>? = null,
    val disableSearch: Boolean = false,
    val enableCitation: Boolean = false,
    val enableTrace: Boolean = false,
    val maxOutputTokens: Int = 1024,
    val responseFormat: String = "text",
    val userIp: String? = null,
    val userId: String? = null,
)

fun GenerateConfig.toQianfanChatRequest(
    messages: List<QianfanMessage>
): QianfanChatRequest {
    return QianfanChatRequest(
        messages,
        this.temperature,
        this.topP,
        this.penaltyScore,
        this.stream,
        this.enableSystemMemory,
        this.systemMemoryId,
        this.system,
        this.stop,
        this.disableSearch,
        this.enableCitation,
        this.enableTrace,
        this.maxOutputTokens,
        this.responseFormat,
        this.userIp,
        this.userId
    )
}

@Serializable
data class QianfanMessage(
    val role: String,
    val content: String
)

fun MessageSender.toQianfanSender() =
    when (this) {
        MessageSender.User -> "user"
        MessageSender.Model -> "assistant"
        MessageSender.System -> "system"
    }