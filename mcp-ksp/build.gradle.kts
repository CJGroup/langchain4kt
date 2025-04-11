plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

dependencies {
    api(project(":langchain4kt2-mcp"))
    implementation(libs.ksp)
    implementation(libs.kotlinpoet)
}