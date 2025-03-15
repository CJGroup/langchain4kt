package io.github.stream29.langchain4kt.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

public inline fun <Input, Output, NewInput, NewOutput> Generator<Input, Output>.wrapped(crossinline block: (Generator<Input, Output>) -> Generator<NewInput, NewOutput>): Generator<NewInput, NewOutput> =
    block(this)

public inline fun <Input, Output, NewOutput> Generator<Input, Output>.mapOutput(crossinline block: suspend (Output) -> NewOutput): Generator<Input, NewOutput> =
    { input -> block(this(input)) }

public inline fun <Input, OutputChunk, NewOutputChunk> Generator<Input, Flow<OutputChunk>>.mapOutputFlow(
    crossinline block: suspend (OutputChunk) -> NewOutputChunk
): Generator<Input, Flow<NewOutputChunk>> = { input -> this(input).map { block(it) } }

public inline fun <Input, NewInput, Output> Generator<Input, Output>.mapInput(crossinline block: suspend (NewInput) -> Input): Generator<NewInput, Output> =
    { input -> this(block(input)) }

public inline fun <InputItem, NewInputItem, Output> Generator<History<InputItem>, Output>.mapInputHistory(crossinline block: suspend (NewInputItem) -> InputItem): Generator<History<NewInputItem>, Output> =
    { input -> this(input.asFlow().map(block).toList()) }

public inline fun <Input, Output> Generator<Input, Output>.onInput(crossinline block: suspend (Input) -> Unit): Generator<Input, Output> =
    mapInput { block(it); it }

public inline fun <Input, Output> Generator<Input, Output>.onOutput(crossinline block: suspend (Output) -> Unit): Generator<Input, Output> =
    mapOutput { block(it); it }

public inline fun <Input, OutputChunk> Generator<Input, Flow<OutputChunk>>.onOutputFlow(crossinline block: suspend (OutputChunk) -> Unit): Generator<Input, Flow<OutputChunk>> =
    mapOutput { it.map { block(it); it } }

public fun <Input : Any, Output> Generator<Input?, Output>.notNullableInput(): Generator<Input, Output> = this

public fun <InputElement, OutputElement> Generator<List<InputElement>, List<OutputElement>>.mapSingle(): Generator<InputElement, OutputElement> =
    { input -> this(listOf(input)).first() }

public fun <InputElement, Output> Generator<List<InputElement>, Output>.mapInputFromSingle(): Generator<InputElement, Output> =
    { input -> this(listOf(input)) }

public fun <Input, OutputElement> Generator<Input, List<OutputElement>>.mapOutputFromSingle(): Generator<Input, OutputElement> =
    { input -> this(input).first() }

public fun <InputElement, Output> Generator<List<InputElement>, Output>.appendInputOn(baseList: List<InputElement>) =
    mapInput { it: List<InputElement> -> baseList + it }

public fun <InputElement, Output> Generator<List<InputElement>, Output>.appendInputOn(baseElement: InputElement) =
    appendInputOn(listOf(baseElement))