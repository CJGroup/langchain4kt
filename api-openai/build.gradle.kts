plugins {
    id("kotlin-multiplatform-convention")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt-core"))
                api(project(":langchain4kt-streaming"))
                api(project(":langchain4kt-embedding"))
                api(libs.openai.client)
                api(libs.ktor.core)
                api(libs.ktor.serialization.kotlinx.json)
            }
        }
        val jvmTest by getting {
            dependencies {
                api(libs.kotlin.test)
                api(libs.streamlin)
                api(libs.ktor.client.cio)
                api(libs.ktor.client.logging)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.client.auth)
                api(libs.logback.classic)
            }
        }
    }
}