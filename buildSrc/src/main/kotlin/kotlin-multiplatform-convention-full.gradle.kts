plugins {
    id("kotlin-multiplatform-convention-base")
}

kotlin {
    configureJvm(8)
    configureJs()
    configureWasm()
    configureNative()
}