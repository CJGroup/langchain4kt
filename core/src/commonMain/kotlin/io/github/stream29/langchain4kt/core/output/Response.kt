package io.github.stream29.langchain4kt.core.output

public data class Response<MetaInfo>(
    val message: String,
    val metaInfo: MetaInfo
)