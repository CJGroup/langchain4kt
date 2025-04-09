package io.github.stream29.langchain4kt2.core

import kotlinx.coroutines.flow.Flow

// Entities
public typealias History<HistoryItem> = List<HistoryItem>
public typealias MutableHistory<HistoryItem> = MutableList<HistoryItem>

// Functors
public typealias Provider<T> = () -> T
public typealias Generator<Request, Response> = suspend (Request) -> Response
public typealias StreamingGenerator<Request, Output> = suspend (Request) -> Flow<Output>