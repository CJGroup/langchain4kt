package chat.output

import chat.message.Message

sealed interface Response<out Content,out SuccessInfo,out FailInfo> {
    data class Success<Content, SuccessInfo>(
        val message: Message<Content>,
        val successInfo: SuccessInfo
    ) : Response<Content, SuccessInfo, Nothing>

    data class Fail<FailInfo>(
        val failInfo: FailInfo
    ) : Response<Nothing, Nothing, FailInfo>
}