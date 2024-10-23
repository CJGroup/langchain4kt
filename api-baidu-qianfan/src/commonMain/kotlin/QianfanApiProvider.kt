import chat.ChatApiProvider
import chat.input.Context
import chat.output.Response
import io.ktor.client.*

class QianfanApiProvider(
    val httpClient: HttpClient,
    val apiKey: String,
    val secretKey: String,
): ChatApiProvider<Unit,Unit> {
    override suspend fun generate(context: Context): Response<*, Unit, Unit> {
        TODO("Not yet implemented")
    }
}