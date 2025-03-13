import com.aallam.openai.api.chat.ChatCompletionRequestBuilder
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.embedding.EmbeddingRequestBuilder
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.stream29.langchain4kt.api.openai.asEmbeddingGenerator
import io.github.stream29.langchain4kt.api.openai.asGenerator
import io.github.stream29.langchain4kt.api.openai.asStreamingGenerator
import io.github.stream29.langchain4kt.core.configure
import io.github.stream29.langchain4kt.core.generateByNotNullable
import io.github.stream29.langchain4kt.core.mapOutput
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
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
    val openAi = OpenAI(clientConfig)

    @Test
    fun `ChatApiProvider test`() {
        val generate = openAi.asGenerator()
            .configure { model = ModelId("qwen-turbo") }
            .generateByNotNullable(ChatCompletionRequestBuilder::messages)
            .mapOutput { it.choices.first().message.content }
        runBlocking(Dispatchers.IO) {
            val response = generate(listOf(ChatMessage.User("hello")))
            println(response)
        }
    }

    @Test
    fun `StreamChatApiProvider test`() {
        val generateStreaming = openAi.asStreamingGenerator()
            .configure { model = ModelId("qwen-turbo") }
            .generateByNotNullable(ChatCompletionRequestBuilder::messages)
            .mapOutput { it.map { it.choices.first().delta?.content } }
        runBlocking {
            val response = generateStreaming(listOf(ChatMessage.User("hello")))
            response.collect { println(it) }
        }
    }

    @Test
    fun `EmbeddingApiProvider test`() {
        val embed = openAi.asEmbeddingGenerator()
            .configure { model = ModelId("text-embedding-v3") }
            .generateByNotNullable(EmbeddingRequestBuilder::input)
            .mapOutput { it.embeddings.map { it.embedding } }
        runBlocking {
            val response = embed(listOf("hello"))
            println(response)
        }
    }
}