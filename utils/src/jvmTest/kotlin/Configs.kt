import io.github.stream29.langchain4kt.api.baiduqianfan.GenerateConfig
import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.api.googlegemini.GeminiApiProvider
import io.github.stream29.langchain4kt.api.googlegemini.GenerationConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val httpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.BODY
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
    }
    install(HttpRequestRetry) {
        retryOnException(maxRetries = 10)
    }
}

val qianfanApiProvider = QianfanApiProvider(
    httpClient = httpClient,
    apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
    secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
    model = "ernie-4.0-8k-latest",
    generateConfig = GenerateConfig(),
)

val geminiApiProvider = GeminiApiProvider(
    httpClient = httpClient,
    apiKey = System.getenv("GOOGLE_AI_GEMINI_API_KEY")!!,
    generationConfig = GenerationConfig(),
    model = "gemini-1.5-flash"
)