package io.github.stream29.langchain4kt.core

import io.github.stream29.langchain4kt.core.input.Context
import io.github.stream29.langchain4kt.core.output.StreamResponse

public interface StreamChatApiProvider<MetaInfo> {
    public suspend fun generate(context: Context): StreamResponse<MetaInfo>
}

