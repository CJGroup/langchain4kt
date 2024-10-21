plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.serialization").version("2.0.10")
    id("module.publication")
}

val ktor_version = "3.0.0"
val logback_version = "1.5.6"

kotlin {
    jvm()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
                implementation("io.github.stream29:streamlin:2.4")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("io.ktor:ktor-client-cio:$ktor_version")
                implementation("io.ktor:ktor-client-logging:$ktor_version")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("ch.qos.logback:logback-classic:$logback_version")
            }
        }
    }
}