package io.github.stream29.langchain4kt.api.baiduqianfan

data class QianFanGenerationException(
    val info: RequestError
) : Exception(info.errorMsg)

data class QianFanTokenFetchException(
    val info: AccessTokenError
) : Exception(info.errorDescription)