plugins {
    id("kotlin-jvm-convention")
    id("publishing-convention")
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/snapshot")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":langchain4kt2-core"))
    implementation(libs.spring.ai.core)
    implementation(libs.kotlinx.coroutine.reactive)
    testImplementation(libs.spring.ai.qianfan)
    testImplementation(libs.kotlin.test)
}