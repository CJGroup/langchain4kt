import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
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
                implementation(project(":langchain4kt-core"))
                implementation(project(":langchain4kt-utils"))
                implementation(libs.coroutines.run.blocking.all)
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project(":langchain4kt-api-baidu-qianfan"))
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