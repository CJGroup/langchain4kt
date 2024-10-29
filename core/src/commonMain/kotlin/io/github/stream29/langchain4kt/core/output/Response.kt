package io.github.stream29.langchain4kt.core.output

data class Response<MetaInfo>(
    val message: String,
    val metaInfo: MetaInfo
)