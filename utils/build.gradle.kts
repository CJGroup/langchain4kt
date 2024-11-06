import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    id("module.publication")
}

kotlin {
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(project(":langchain4kt-core"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.script.runtime)
                implementation(project(":langchain4kt-core"))
                implementation(project(":langchain4kt-api-baidu-qianfan"))
                implementation(project(":langchain4kt-api-google-gemini"))
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