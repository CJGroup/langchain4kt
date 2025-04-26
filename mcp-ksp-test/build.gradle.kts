plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
}

dependencies {
    api(project(":langchain4kt2-mcp"))
    ksp(project(":langchain4kt2-mcp-ksp"))
    testImplementation(libs.kotlin.test)
}