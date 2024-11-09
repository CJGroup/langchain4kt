plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}

dependencies {
    testImplementation(libs.langchain4j)
    testImplementation(libs.kotlin.test)
}