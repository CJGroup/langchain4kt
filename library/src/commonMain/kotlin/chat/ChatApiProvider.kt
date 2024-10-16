package chat

import chat.input.Context
import chat.output.Response

interface IChatApiProvider<SuccessInfo, FailInfo> {
    fun generate(context: Context): Response<*, SuccessInfo, FailInfo>
}