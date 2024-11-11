import dev.langchain4j.model.dashscope.QwenChatModel
import io.github.stream29.langchain4kt.api.langchain4kt.Langchain4jApiProvider
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

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
        requestTimeout = 200 * 1000
        proxy = ProxyBuilder.http("https://127.0.0.1:7890")
    }
}

val apiProvider = Langchain4jApiProvider(
    QwenChatModel.builder()
        .apiKey(System.getenv("ALIBABA_QWEN_API_KEY")!!)
        .modelName("qwen-plus")
        .build()
)