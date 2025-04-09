plugins {
    id("kotlin-multiplatform-convention-base")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

kotlin {
    configureJvm(17)
    configureJs()
    configureIosForGemini()
    configureWasmForKtor()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt2-core"))
                api(libs.google.generative.ai)
                api(libs.google.generative.ai.common)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}