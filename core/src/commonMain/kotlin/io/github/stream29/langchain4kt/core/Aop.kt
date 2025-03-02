package io.github.stream29.langchain4kt.core

public inline fun <Input, Output, NewInput, NewOutput> Generator<Input, Output>.wrapped(crossinline block: (Generator<Input, Output>) -> Generator<NewInput, NewOutput>): Generator<NewInput, NewOutput> =
    block(this)

public inline fun <Input, Output, NewOutput> Generator<Input, Output>.handleOutput(crossinline block: suspend (Output) -> NewOutput): Generator<Input, NewOutput> =
    { input -> block(this(input)) }

public inline fun <Input, NewInput, Output> Generator<Input, Output>.handleInput(crossinline block: suspend (NewInput) -> Input): Generator<NewInput, Output> =
    { input -> this(block(input)) }

public inline fun <Input, Output> Generator<Input, Output>.onInput(crossinline block: suspend (Input) -> Unit): Generator<Input, Output> =
    handleInput { block(it); it }

public inline fun <Input, Output> Generator<Input, Output>.onOutput(crossinline block: suspend (Output) -> Unit): Generator<Input, Output> =
    handleOutput { block(it); it }
