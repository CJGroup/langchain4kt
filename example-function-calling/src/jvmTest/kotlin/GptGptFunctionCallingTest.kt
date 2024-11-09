import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.core.asChatModel
import io.github.stream29.langchain4kt.example.functioncalling.*
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import kotlin.test.Test

class GptGptFunctionCallingTest {
    @Test
    fun simpleFunctionCall() {
        val model = FunctionCallingModel(
            qianfanApiProvider.asChatModel {
                systemInstruction(functionCallPrompt(functions))
            },
            functions
        )
        runBlocking {
            model.chat("现在几点？")
        }
        println(model.context)
    }
}

val functions = listOf(
    GptFunction {
        name("查询现在时间")
        description("返回现在的时间")
        resolveWith { _ -> LocalDateTime.now().toString() }
    }.exampleExplained(),
)

val httpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.ALL
        logger = Logger.SIMPLE
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    engine {
        requestTimeout = 20 * 1000
        proxy = ProxyBuilder.http("https://127.0.0.1:7890")
    }
}

val qianfanApiProvider = QianfanApiProvider(
    httpClient = httpClient,
    apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
    secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
    model = "ernie-4.0-8k-latest",
)