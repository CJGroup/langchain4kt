plugins {
    alias(libs.plugins.kotlinJvm)
    id("module.publication")
}

kotlin {
    explicitApi()
    jvmToolchain(17)
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/snapshot")
}

dependencies {
    implementation(project(":langchain4kt-core"))
    implementation(project(":langchain4kt-streaming"))
    implementation(project(":langchain4kt-embedding"))
    implementation(libs.spring.ai.core)
    testImplementation(libs.spring.ai.qianfan)
    testImplementation(libs.kotlin.test)
}