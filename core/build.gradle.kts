plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.serialization").version("2.0.10")
    id("module.publication")
}

kotlin {
    jvm()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
    }
}