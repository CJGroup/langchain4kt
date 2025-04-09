package io.github.stream29.langchain4kt2.core

@RequiresOptIn(message = "This is experimental and might be changed in future.", level = RequiresOptIn.Level.WARNING)
@Target(
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FIELD,
    AnnotationTarget.CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalLangchain4ktApi