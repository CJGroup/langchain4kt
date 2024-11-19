plugins {
    alias(libs.plugins.kotlinJvm)
    id("module.publication")
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}

dependencies {
    implementation(project(":langchain4kt-core"))
    implementation(project(":langchain4kt-streaming"))
    implementation(project(":langchain4kt-embedding"))
    implementation(libs.langchain4j.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.langchain4j.dashscope)
}