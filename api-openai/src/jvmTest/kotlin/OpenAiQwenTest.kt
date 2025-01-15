import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.stream29.langchain4kt.api.openai.OpenAiChaiApiProvider
import io.github.stream29.langchain4kt.api.openai.OpenAiEmbeddingApiProvider
import io.github.stream29.langchain4kt.api.openai.OpenAiGenerationConfig
import io.github.stream29.langchain4kt.api.openai.OpenAiStreamChatProvider
import io.github.stream29.langchain4kt.core.generateFrom
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.streaming.generateFrom
import io.github.stream29.streamlin.prettyPrint
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class OpenAiQwenTest {
    val clientConfig = OpenAIConfig(
        token = System.getenv("ALIBABA_QWEN_API_KEY")!!,
        host = OpenAIHost(
            baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/completions",
        ),
        engine = CIO.create(),
        httpClientConfig = {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        prettyPrint(message)
                    }
                }
            }
        }
    )
    val generationConfig = OpenAiGenerationConfig("qwen-turbo")

    @Test
    fun `ChatApiProvider test`() {
        val chatApiProvider = OpenAiChaiApiProvider(
            clientConfig,
            generationConfig
        )
        runBlocking {
            val response = chatApiProvider.generateFrom("hello")
            println(response)
        }
    }

    @Test
    fun `StreamChatApiProvider test`() {
        val streamChatProvider = OpenAiStreamChatProvider(
            clientConfig,
            generationConfig
        )
        runBlocking {
            val response = streamChatProvider.generateFrom { MessageSender.User.chat("hello") }
            response.message.collect { println(it) }
            response.metaInfo.await().let { prettyPrint(it) }
        }
    }

    @Test
    fun `EmbeddingApiProvider test`() {
        val embeddingApiProvider = OpenAiEmbeddingApiProvider(
            clientConfig,
            OpenAiGenerationConfig("text-embedding-v3")
        )
        runBlocking {
            val response = embeddingApiProvider.embed("hello")
            prettyPrint(response)
        }
    }
}