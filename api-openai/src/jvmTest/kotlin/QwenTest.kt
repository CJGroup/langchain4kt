import com.aallam.openai.api.logging.Logger
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.stream29.langchain4kt2.core.ModelTextMessage
import io.github.stream29.langchain4kt2.core.configure
import io.github.stream29.langchain4kt2.core.mapOutput
import io.github.stream29.langchain4kt2.core.mapOutputFlow
import io.github.stream29.langchain4kt2.core.mapSingle
import io.github.stream29.langchain4kt2.api.openai.asEmbeddingGenerator
import io.github.stream29.langchain4kt2.api.openai.asGenerator
import io.github.stream29.langchain4kt2.api.openai.asStreamingGenerator
import io.github.stream29.langchain4kt2.api.openai.generateByInput
import io.github.stream29.langchain4kt2.api.openai.generateByMessages
import io.github.stream29.langchain4kt2.api.openai.mapInputFromText
import io.github.stream29.langchain4kt2.api.openai.mapUnion
import io.github.stream29.langchain4kt2.api.openai.singleText
import io.github.stream29.union.cast
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class QwenTest {
    val clientConfig = OpenAIConfig(
        logging = LoggingConfig(logger = Logger.Empty),
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
            .mapUnion()
            .mapInputFromText()
            .mapOutput { it.cast<ModelTextMessage>().text }
        runBlocking(Dispatchers.IO) {
            val response = generate("hello")
            println(response)
        }
    }

    @Test
    fun `StreamChatApiProvider test`() {
        val generateStreaming = openAi.asStreamingGenerator()
            .configure { model = ModelId("qwen-turbo") }
            .generateByMessages()
            .mapInputFromText()
            .mapOutputFlow { it.singleText() }
        runBlocking {
            val response = generateStreaming("hello")
            response.collect { println(it) }
        }
    }

    @Test
    fun `EmbeddingApiProvider test`() {
        val embed = openAi.asEmbeddingGenerator()
            .configure { model = ModelId("text-embedding-v3") }
            .generateByInput()
            .mapOutput { it.embeddings }
            .mapSingle()
            .mapOutput { it.embedding }
        runBlocking {
            val response = embed("hello")
            println(response)
        }
    }
}