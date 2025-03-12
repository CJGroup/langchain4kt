import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.stream29.langchain4kt.api.openai.OpenAiApiProvider
import io.github.stream29.langchain4kt.api.openai.OpenAiEmbeddingGenerator
import io.github.stream29.langchain4kt.api.openai.OpenAiGenerationConfig
import io.github.stream29.langchain4kt.api.openai.OpenAiStreamingApiProvider
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class OpenAiQwenTest {
    val clientConfig = OpenAIConfig(
        token = System.getenv("ALIBABA_QWEN_API_KEY")!!,
        host = OpenAIHost(
            baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/completions",
        ),
        engine = CIO.create(),
    )
    val generationConfig = OpenAiGenerationConfig("qwen-turbo")

    @Test
    fun `ChatApiProvider test`() {
        val chatApiProvider = OpenAiApiProvider(
            clientConfig,
            generationConfig
        )
        runBlocking(Dispatchers.IO) {
            val response = chatApiProvider(listOf(ChatMessage.User("hello")))
            println(response.choices.first().message.content)
        }
    }

    @Test
    fun `StreamChatApiProvider test`() {
        val streamChatProvider = OpenAiStreamingApiProvider(
            clientConfig,
            generationConfig
        )
        runBlocking {
            val response = streamChatProvider(listOf(ChatMessage.User("hello")))
            response.collect { println(it.choices.first().delta?.content) }
        }
    }

    @Test
    fun `EmbeddingApiProvider test`() {
        val embeddingApiProvider = OpenAiEmbeddingGenerator(
            clientConfig,
            OpenAiGenerationConfig("text-embedding-v3")
        )
        runBlocking {
            val response = embeddingApiProvider(listOf("hello"))
            println(response)
        }
    }
}