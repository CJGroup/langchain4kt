plugins {
    id("kotlin-multiplatform-convention-base")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

kotlin {
    configureJvm(8)
    configureJs()
    configureWasmForKtor()
    configureNativeForOpenAi()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt-core"))
                api(libs.ktor.core)
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.streamlin)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
            }
        }
    }
}