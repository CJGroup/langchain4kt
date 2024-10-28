package io.github.stream29.langchain4kt.api.baiduqianfan

import io.github.stream29.langchain4kt.core.ChatApiProvider
import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.message.MessageSender
import io.github.stream29.langchain4kt.core.message.MessageType
import io.github.stream29.langchain4kt.core.message.TextMessage
import io.github.stream29.langchain4kt.core.output.Response
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class QianfanApiProvider(
    val httpClient: HttpClient,
    val model: String,
    val apiKey: String,
    val secretKey: String,
    val generateConfig: GenerateConfig,
) : ChatApiProvider<Unit, String> {
    var accessToken: String? = null
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }
    override suspend fun generate(context: Context): Response<Message<*>, Unit, String> {
        if (accessToken == null) {
            when (val response = httpClient.getAccessToken(apiKey, secretKey, json)) {
                is Response.Success -> accessToken = response.content
                is Response.Failure -> return Response.Failure(response.failInfo)
            }
        }
        val messages = context.history
            .asSequence()
            .filter { it.type == MessageType.Text }
            .map {
                QianfanMessage(
                    it.sender.toQianfanSender(),
                    it.content.toString()
                )
            }.toList()
        val request = generateConfig.toQianfanChatRequest(messages)
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
            return Response.Success(
                content = TextMessage(
                    sender = MessageSender.Model,
                    content = body.result
                ),
                successInfo = Unit
            )
        } catch (e: SerializationException) {
            val error = json.decodeFromString<RequestError>(responseBody)
            return Response.Failure(
                failInfo = error.toString()
            )
        } catch (e: Exception) {
            return Response.Failure(
                failInfo = e.stackTraceToString()
            )
        }
    }
}

private suspend fun HttpClient.getAccessToken(
    apiKey: String,
    secretKey: String,
    json: Json
): Response<String, AccessTokenResponse, String> {
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
        return Response.Success(
            content = response.accessToken,
            successInfo = response
        )
    } catch (e: SerializationException) {
        val error = json.decodeFromString<AccessTokenError>(responseBody)
        return Response.Failure(
            failInfo = error.toString()
        )
    } catch (e: Exception) {
        return Response.Failure(
            failInfo = e.stackTraceToString()
        )
    }
}

private const val accessTokenAuthUrl = "https://aip.baidubce.com/oauth/2.0/token"
private const val chatUrl = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/"
