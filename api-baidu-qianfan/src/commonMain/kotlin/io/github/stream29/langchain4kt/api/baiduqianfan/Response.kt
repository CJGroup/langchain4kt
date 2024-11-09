package io.github.stream29.langchain4kt.api.baiduqianfan

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Error response for the access token request.
 */
@Serializable
public data class QianfanAccessTokenError(
    @SerialName("error_description")
    val errorDescription: String,
    val error: String
)

/**
 * Error response for the request.
 */
@Serializable
public data class QianfanRequestError(
    @SerialName("error_code")
    val errorCode: Int,
    @SerialName("error_msg")
    val errorMsg: String
)

/**
 * Raw response from QianFan chat API.
 */
@Serializable
public data class QianfanChatResponse(
    val id: String,
    val `object`: String,
    val created: Int,
    @SerialName("sentence_id")
    val sentenceId: Int? = null,
    @SerialName("is_end")
    val isEnd: Boolean? = null,
    @SerialName("is_truncated")
    val isTruncated: Boolean,
    @SerialName("finish_reason")
    val finishReason: String,
    @SerialName("search_info")
    val searchInfo: QianfanSearchInfo? = null,
    val result: String,
    @SerialName("need_clear_history")
    val needClearHistory: Boolean,
    @SerialName("flag")
    val flag: Int? = null,
    @SerialName("ban_round")
    val banRound: Int? = null,
    val usage: QianfanUsage
)

/**
 * Search information in the response.
 */
@Serializable
public data class QianfanSearchInfo(
    @SerialName("search_results")
    val searchResults: List<QianfanSearchResult>
)

/**
 * Search result in the response.
 */
@Serializable
public data class QianfanSearchResult(
    val index: Int,
    val url: String,
    val title: String
)

/**
 * Usage of tokens in the response.
 */
@Serializable
public data class QianfanUsage(
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)

@Serializable
internal data class AccessTokenResponse(
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("session_key")
    val sessionKey: String,
    @SerialName("access_token")
    val accessToken: String,
    val scope: String,
    @SerialName("session_secret")
    val sessionSecret: String
)