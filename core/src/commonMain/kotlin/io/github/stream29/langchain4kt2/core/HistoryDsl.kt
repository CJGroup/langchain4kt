package io.github.stream29.langchain4kt2.core


public fun TextMessage(direction: DataDirection, text: String): TextMessage =
    when (direction) {
        DataDirection.Input -> UserTextMessage(text)
        DataDirection.Output -> ModelTextMessage(text)
    }

public fun UrlImageMessage(direction: DataDirection, url: String): UrlImageMessage =
    when (direction) {
        DataDirection.Input -> UserUrlImageMessage(url)
        DataDirection.Output -> ModelUrlImageMessage(url)
    }