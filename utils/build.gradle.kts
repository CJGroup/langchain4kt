plugins {
    id("kotlin-multiplatform-convention")
    alias(libs.plugins.kotlinSerialization)
    id("module.publication")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt-core"))
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