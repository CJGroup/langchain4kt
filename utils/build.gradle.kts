plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("module.publication")
}

kotlin {
    jvm()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(project(":core"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.script.runtime)
                implementation(project(":core"))
                implementation(project(":api-baidu-qianfan"))
                implementation(project(":api-google-gemini"))
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlin.test)
                implementation(libs.streamlin)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.logback.classic)
            }
        }
    }
}