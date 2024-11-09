package io.github.stream29.langchain4kt.api.baiduqianfan

/**
 * Exception for QianFan generation.
 */
public data class QianFanGenerationException(
    val info: QianfanRequestError
) : Exception(info.errorMsg)

/**
 * Exception for QianFan token fetch.
 */
public data class QianFanTokenFetchException(
    val info: QianfanAccessTokenError
) : Exception(info.errorDescription)