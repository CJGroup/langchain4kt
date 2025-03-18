package io.github.stream29.langchain4kt2.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.iterator

public object DispatchStrategy {
    public fun <Input, Output> iterateOnInfinite(block: suspend SequenceScope<Generator<Input, Output>>.() -> Unit): Generator<Input, Output> {
        val mutex = Mutex()
        val iterator = sequence(block).iterator()
        return { input -> mutex.withLock { iterator.next() }(input) }
    }

    public fun <Input, Output> pollOn(source: Collection<Generator<Input, Output>>): Generator<Input, Output> {
        checkNotEmpty(source)
        return iterateOnInfinite {
            while (true) {
                for (generator in source)
                    yield(generator)
            }
        }
    }

    public fun <Input, Output> weightedPollOn(source: Map<Generator<Input, Output>, Int>): Generator<Input, Output> {
        require(source.values.sum() > 0) { "Generators must not be empty" }
        return iterateOnInfinite {
            while (true) {
                for ((generator, weight) in source)
                    repeat(weight) { yield(generator) }
            }
        }
    }

    public fun <Input, Output> randomOn(source: List<Generator<Input, Output>>): Generator<Input, Output> {
        checkNotEmpty(source)
        return { input -> source.random().invoke(input) }
    }

    public fun <Input, Output> weightedRandomOn(source: Map<Generator<Input, Output>, Int>): Generator<Input, Output> {
        val weightedSource = sequence {
            for ((generator, weight) in source)
                repeat(weight) { yield(generator) }
        }.toList()
        return randomOn(weightedSource)
    }

    private fun checkNotEmpty(source: Collection<*>) {
        require(source.isNotEmpty()) { "Generators must not be empty" }
    }
}