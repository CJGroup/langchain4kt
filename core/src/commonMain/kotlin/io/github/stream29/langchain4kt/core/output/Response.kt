package io.github.stream29.langchain4kt.core.output

sealed interface Response<out Content,out SuccessInfo,out FailureInfo> {
    data class Success<Content, SuccessInfo>(
        val content: Content,
        val successInfo: SuccessInfo
    ) : Response<Content, SuccessInfo, Nothing>

    data class Failure<FailureInfo>(
        val failInfo: FailureInfo
    ) : Response<Nothing, Nothing, FailureInfo>
}