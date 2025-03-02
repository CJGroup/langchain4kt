plugins {
    id("kotlin-jvm-convention")
    id("publishing-convention")
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":langchain4kt-core"))
    implementation(libs.langchain4j.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.langchain4j.dashscope)
}