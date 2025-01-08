import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.stream29.langchain4kt.api.openai.OpenAiChaiApiProvider
import io.github.stream29.langchain4kt.core.generateFrom
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.appendIfNameAbsent
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.test.Test

class DeepSeekTest {
    val openAiClient = OpenAI(
        OpenAIConfig(
            token = System.getenv("DEEPSEEK_API_KEY")!!,
            logging = LoggingConfig(
                logLevel = LogLevel.All,
                logger = Logger.Simple,
            ),
            host = OpenAIHost(
                baseUrl = "https://api.deepseek.com",
            ),
            engine = CIO.create(),
            httpClientConfig = {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        }
                    )
                }
            }
        )
    )
    val chatApiProvider = OpenAiChaiApiProvider(openAiClient, "deepseek-chat")

    @Test
    fun invoke() {
        runBlocking {
            val response = chatApiProvider.generateFrom("hello")
            println(response)
        }
    }
}