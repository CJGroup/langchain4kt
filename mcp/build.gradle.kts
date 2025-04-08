plugins {
    id("kotlin-jvm-convention")
    alias(libs.plugins.kotlinSerialization)
    id("publishing-convention")
}

dependencies {
    api(project(":langchain4kt2-core"))
    api(libs.kunion)
    api(libs.mcp.sdk)
    api(libs.json.schema.generator)
    api(libs.kotlinx.coroutine.core)
    api(libs.kotlinx.serialization.core)
    testImplementation(libs.kotlin.test)
}

kotlin {

}