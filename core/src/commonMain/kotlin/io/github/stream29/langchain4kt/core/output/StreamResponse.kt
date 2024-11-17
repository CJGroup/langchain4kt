package io.github.stream29.langchain4kt.core.output

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow

public data class StreamResponse<MetaInfo>(
    val message: Flow<String>,
    val metaInfo: Deferred<MetaInfo>
)
