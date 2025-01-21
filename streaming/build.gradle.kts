plugins {
    id("kotlin-multiplatform-convention-full")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":langchain4kt-core"))
            }
        }
    }
}