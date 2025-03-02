package io.github.stream29.langchain4kt.core

import kotlinx.coroutines.flow.Flow

// Entities
public typealias History<HistoryItem> = List<HistoryItem>
public typealias MutableHistory<HistoryItem> = MutableList<HistoryItem>

// Functors
public typealias Generator<Input, Output> = suspend (Input) -> Output
public typealias ApiProvider<HistoryItem, Output> = Generator<History<HistoryItem>, Output>
public typealias ChatApiProvider<Content, Output> = ApiProvider<ChatMessage<Content>, Output>
public typealias StreamChatApiProvider<Content, Output> = ChatApiProvider<Content, Flow<Output>>