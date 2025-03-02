package io.github.stream29.langchain4kt.core

public inline fun <HistoryItem> buildHistory(block: MutableHistory<HistoryItem>.() -> Unit): History<HistoryItem> {
    return mutableListOf<HistoryItem>().apply(block)
}

public fun <Content> MutableHistory<ChatMessage<Content>>.input(content: Content) {
    add(ChatMessage(DataDirection.Input, content))
}

public fun <Content> MutableHistory<ChatMessage<Content>>.output(content: Content) {
    add(ChatMessage(DataDirection.Output, content))
}