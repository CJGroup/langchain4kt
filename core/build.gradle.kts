plugins {
    id("kotlin-multiplatform-convention")
    alias(libs.plugins.kotlinSerialization)
    id("module.publication")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutine.core)
                api(libs.kotlinx.serialization.core)
            }
        }
    }
}