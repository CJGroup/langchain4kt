plugins {
    id("kotlin-multiplatform-convention-full")
    id("publishing-convention")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":langchain4kt2-core"))
            }
        }
        jvmTest {
            dependencies {
                api(libs.kotlin.test)
            }
        }
    }
}