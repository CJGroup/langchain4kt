pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "langchain4kt-root"
prefixedModule("core")
prefixedModule("utils")
prefixedModule("api-google-gemini")
prefixedModule("api-baidu-qianfan")
prefixedModule("api-langchain4j")
prefixedModule("api-springai")
prefixedModule("api-openai")

fun prefixedModule(name: String) {
    val modulePrefix = "langchain4kt-"
    val prefixedName = modulePrefix + name
    include(":$prefixedName")
    project(":$prefixedName").projectDir = file("./$name")
}
