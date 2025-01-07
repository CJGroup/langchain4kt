plugins {
    id("kotlin-multiplatform-convention")
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    sourceSets {
        val jvmTest by getting {
            dependencies {
                implementation(project(":langchain4kt-core"))
                implementation(project(":langchain4kt-api-langchain4j"))
                implementation(libs.langchain4j.dashscope)
                implementation(libs.kotlin.test)
                implementation(libs.logback.classic)
            }
        }
    }
}