plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}

dependencies {
    testImplementation(libs.kotlin.test)
}