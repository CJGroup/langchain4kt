package io.github.stream29.langchain4kt.core.output

import io.github.stream29.langchain4kt.core.ChatApiProvider

/**
 * Response object that is returned by [ChatApiProvider].
 */
public data class Response<out MetaInfo>(
    val message: String,
    val metaInfo: MetaInfo
)