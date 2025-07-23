plugins {
    id("kotlin-multiplatform-convention-base")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    configureJvm(8)
    configureJs()
    configureWasm()
    configureNative()
}