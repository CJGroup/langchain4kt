import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanApiProvider
import io.github.stream29.langchain4kt.api.baiduqianfan.QianfanGenerationConfig
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

val qianfanApiProvider = QianfanApiProvider(
    httpClient = httpClient,
    apiKey = System.getenv("BAIDU_QIANFAN_API_KEY")!!,
    secretKey = System.getenv("BAIDU_QIANFAN_SECRET_KEY")!!,
    model = "ernie-4.0-8k-latest",
    generationConfig = QianfanGenerationConfig(
        stop = listOf(stopSequence),
        disableSearch = true
    )
)