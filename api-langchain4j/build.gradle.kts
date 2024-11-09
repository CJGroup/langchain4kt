plugins {
    alias(libs.plugins.kotlinJvm)
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}

dependencies {
    compileOnly(project(":langchain4kt-core"))
    implementation(libs.langchain4j.core)
    testImplementation(project(":langchain4kt-core"))
    testImplementation(libs.kotlin.test)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    testImplementation(libs.langchain4j.dashscope)
}