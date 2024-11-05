package io.github.stream29.langchain4kt.api.baiduqianfan

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.Response
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

data class QianfanApiProvider(
    val httpClient: HttpClient,
    val model: String,
    val apiKey: String,
    val secretKey: String,
    val generationConfig: QianfanGenerationConfig = QianfanGenerationConfig(),
) : ChatApiProvider<QianfanChatResponse> {
    private val mutex = Mutex()
    var accessToken: String? = null
    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun generate(context: Context): Response<QianfanChatResponse> {
        mutex.withLock {
            if (accessToken == null) {
                accessToken = httpClient.getAccessToken(apiKey, secretKey, json)
            }
        }
        val messages = context.history
            .map {
                QianfanMessage(
                    it.sender.toQianfanSender(),
                    it.content
                )
            }
        val request = generationConfig.toQianfanChatRequest(messages, context.systemInstruction)
        val responseBody = httpClient.post(chatUrl) {
            url {
                appendPathSegments(model)
                parameters.apply {
                    append("access_token", accessToken!!)
                }
            }
            contentType(ContentType.Application.Json)
            setBody(request)
        }.bodyAsText()
        try {
            val body = json.decodeFromString<QianfanChatResponse>(responseBody)
            return Response(
                message = body.result,
                metaInfo = body
            )
        } catch (e: SerializationException) {
            val error = json.decodeFromString<RequestError>(responseBody)
            if(error.errorCode == 110) {
                accessToken = null
                return generate(context)
            }
            throw QianFanGenerationException(error)
        }
    }
}

private fun QianfanGenerationConfig.toQianfanChatRequest(
    messages: List<QianfanMessage>,
    system: String? = null
): QianfanChatRequest {
    return QianfanChatRequest(
        messages,
        this.temperature,
        this.topP,
        this.penaltyScore,
        this.enableSystemMemory,
        this.systemMemoryId,
        system,
        this.stop,
        this.disableSearch,
        this.enableCitation,
        this.enableTrace,
        this.maxOutputTokens,
        this.responseFormat,
        this.userIp,
        this.userId
    )
}

private suspend fun HttpClient.getAccessToken(
    apiKey: String,
    secretKey: String,
    json: Json
): String {
    val responseBody = this.post(accessTokenAuthUrl) {
        url {
            parameters.apply {
                append("grant_type", "client_credentials")
                append("client_id", apiKey)
                append("client_secret", secretKey)
            }
        }
    }.bodyAsText()
    try {
        val response = json.decodeFromString<AccessTokenResponse>(responseBody)
        return response.accessToken
    } catch (e: SerializationException) {
        val error = json.decodeFromString<AccessTokenError>(responseBody)
        throw QianFanTokenFetchException(error)
    }
}

private const val accessTokenAuthUrl = "https://aip.baidubce.com/oauth/2.0/token"
private const val chatUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/"
