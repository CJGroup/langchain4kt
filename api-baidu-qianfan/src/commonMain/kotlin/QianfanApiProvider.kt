import chat.ChatApiProvider
import chat.input.Context
import chat.message.Message
import chat.output.Response
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class QianfanApiProvider(
    val httpClient: HttpClient,
    val apiKey: String,
    val secretKey: String,
) : ChatApiProvider<Unit, Unit> {
    override suspend fun generate(context: Context): Response<Message<*>, Unit, Unit> {
        TODO("Not yet implemented")
    }
}

private suspend fun HttpClient.getAccessToken(
    apiKey: String,
    secretKey: String,
    json: Json
): Response<String, AccessTokenResponse, AccessTokenError> {
    val responseBody = this.post("https://aip.baidubce.com/oauth/2.0/token") {
        url {
            parameters.apply {
                append("grant_type", "client_credentials")
                append("client_id", apiKey)
                append("client_secret", secretKey)
            }
        }
    }.bodyAsText()
    runCatching {
        val response = json.decodeFromString<AccessTokenResponse>(responseBody)
        return Response.Success(
            content = response.accessToken,
            successInfo = response
        )
    }
}