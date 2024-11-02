package io.github.stream29.langchain4kt.api.baiduqianfan

import io.github.stream29.langchain4kt.core.message.MessageSender
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//"messages": [
//{"role":"user", "content": "给我推荐一些自驾游路线"}
//],
//"stream": true

@Serializable
data class QianfanChatRequest(
    val messages: List<QianfanMessage>,
    val temperature: Float = 0.8f,
    @SerialName("top_p")
    val topP: Float = 0.8f,
    @SerialName("penalty_score")
    val penaltyScore: Float = 1.0f,
    @SerialName("enable_system_memory")
    val enableSystemMemory: Boolean = false,
    @SerialName("system_memory_id")
    val systemMemoryId: String? = null,
    val system: String? = null,
    val stop: List<String>? = null,
    @SerialName("disable_search")
    val disableSearch: Boolean = false,
    @SerialName("enable_citation")
    val enableCitation: Boolean = false,
    @SerialName("enable_trace")
    val enableTrace: Boolean = false,
    @SerialName("max_output_tokens")
    val maxOutputTokens: Int = 1024,
    @SerialName("response_format")
    val responseFormat: String = "text",
    @SerialName("user_ip")
    val userIp: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
)

@Serializable
data class GenerateConfig(
    val temperature: Float = 0.8f,
    val topP: Float = 0.8f,
    val penaltyScore: Float = 1.0f,
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
data class QianfanMessage(
    val role: String,
    val content: String
)

fun MessageSender.toQianfanSender() =
    when (this) {
        MessageSender.User -> "user"
        MessageSender.Model -> "assistant"
    }