plugins {
    id("kotlin-multiplatform-convention-full")
    id("publishing-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutine.core)
                api(libs.kotlinx.serialization.core)
                api(libs.kotlinx.serialization.json)
            }
        }
    }
}