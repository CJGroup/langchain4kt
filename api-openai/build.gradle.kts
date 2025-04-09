plugins {
    id("kotlin-multiplatform-convention-base")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

kotlin {
    configureJvm(11)
    configureJs()
    configureWasmForKtor()
    configureNativeForOpenAi()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt2-core"))
                api(libs.openai.client)
                api(libs.ktor.core)
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
        val jvmTest by getting {
            dependencies {
                api(libs.kotlin.test)
                api(libs.ktor.client.cio)
                api(libs.ktor.client.logging)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.client.auth)
            }
        }
    }
}