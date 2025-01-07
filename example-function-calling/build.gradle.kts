plugins {
    id("kotlin-multiplatform-convention")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":langchain4kt-core"))
                implementation(project(":langchain4kt-utils"))
                implementation(libs.coroutines.run.blocking.all)
                implementation(libs.ktor.core)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(project(":langchain4kt-api-baidu-qianfan"))
                implementation(libs.kotlin.test)
                implementation(libs.streamlin)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.logback.classic)
                implementation("org.jetbrains.kotlin:kotlin-scripting-common")
                implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
                implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
                implementation("org.jetbrains.kotlin:kotlin-scripting-jsr223")
            }
        }
    }
}