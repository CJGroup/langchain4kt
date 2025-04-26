plugins {
    id("kotlin-jvm-convention")
    id("publishing-convention")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    api(project(":langchain4kt2-core"))
    api(libs.langchain4j.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.langchain4j.dashscope)
}