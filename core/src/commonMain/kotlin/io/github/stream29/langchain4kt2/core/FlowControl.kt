package io.github.stream29.langchain4kt2.core

import kotlinx.coroutines.sync.Semaphore

public fun <Input, Output> Generator<Input, Output>.limitedParallelism(max: Int): Generator<Input, Output> {
    require(max > 0) { "max should be greater than 0" }
    val semaphore = Semaphore(max)
    return onInputSuspend { semaphore.acquire() }
        .onOutputSuspend { semaphore.release() }
}