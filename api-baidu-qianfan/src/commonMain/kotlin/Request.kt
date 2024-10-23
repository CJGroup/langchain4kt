import chat.message.MessageSender
import kotlinx.serialization.Serializable

//"messages": [
//{"role":"user","content":"介绍一下北京"}
//]

@Serializable
data class QianfanChatRequest(
    val messages: List<QianfanMessage>
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
        MessageSender.System -> "system"
    }