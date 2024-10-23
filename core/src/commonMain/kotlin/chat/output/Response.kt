package chat.output

import chat.message.Message

sealed interface Response<out Content,out SuccessInfo,out FailureInfo> {
    data class Success<Content, SuccessInfo>(
        val message: Message<Content>,
        val successInfo: SuccessInfo
    ) : Response<Content, SuccessInfo, Nothing>

    data class Failure<FailureInfo>(
        val failInfo: FailureInfo
    ) : Response<Nothing, Nothing, FailureInfo>
}