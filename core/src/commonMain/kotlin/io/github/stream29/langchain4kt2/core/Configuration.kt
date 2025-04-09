package io.github.stream29.langchain4kt2.core

import kotlin.reflect.KMutableProperty1

public interface ConfiguredGenerator<Request, Response> {
    public val requestProvider: Provider<Request>
    public val generator: Generator<Request, Response>
}

public class ConfigurableGenerator<Request, Response>(
    public override val requestProvider: Provider<Request>,
    public override val generator: Generator<Request, Response>,
) : Generator<Request, Response> by generator, ConfiguredGenerator<Request, Response>

public fun <Request, Response> ConfiguredGenerator<Request, Response>.configure(
    buildRequest: Request.() -> Unit
): ConfiguredGenerator<Request, Response> =
    ConfigurableGenerator({ requestProvider().apply(buildRequest) }, generator)

public fun <Request, Input, Response> ConfiguredGenerator<Request, Response>.generateBy(
    transform: suspend Request.(Input) -> Unit,
): Generator<Input, Response> = { input -> generator(requestProvider().apply { transform(input) }) }

public fun <Request, Input, Response> ConfiguredGenerator<Request, Response>.generateBy(
    property: KMutableProperty1<Request, Input>
) = generateBy { input: Input -> property.set(this, input) }

public fun <Request, Input: Any, Response> ConfiguredGenerator<Request, Response>.generateByNotNullable(
    property: KMutableProperty1<Request, Input?>
) = generateBy { input: Input -> property.set(this, input) }