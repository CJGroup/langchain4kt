package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.message.Message
import io.github.stream29.langchain4kt.core.output.Response

interface ChatApiProvider<SuccessInfo, FailInfo> {
    suspend fun generate(context: Context): Response<Message<*>, SuccessInfo, FailInfo>
}