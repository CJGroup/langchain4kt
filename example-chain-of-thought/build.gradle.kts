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
        val jvmTest by getting {
            dependencies {
                implementation(project(":langchain4kt-core"))
                implementation(project(":langchain4kt-api-langchain4j"))
                implementation(libs.langchain4j.dashscope)
                implementation(libs.kotlin.test)
                implementation(libs.logback.classic)
            }
        }
    }
}