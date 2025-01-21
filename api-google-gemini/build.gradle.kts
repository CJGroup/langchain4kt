plugins {
    id("kotlin-multiplatform-convention-base")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

kotlin {
    configureJvm(17)
    configureJs()
    configureWasmForKtor()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt-core"))
                api(project(":langchain4kt-streaming"))
                api(project(":langchain4kt-embedding"))
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