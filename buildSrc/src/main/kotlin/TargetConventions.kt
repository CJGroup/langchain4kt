import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JsModuleKind
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithTests
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.konan.target.HostManager

fun KotlinMultiplatformExtension.configureJvm(jdkVersion: Int) {
    jvm {
        withJava()
    }
    jvmToolchain(jdkVersion)
}

fun KotlinMultiplatformExtension.configureJs() {
    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "10s"
                }
            }
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            sourceMap = true
            moduleKind = JsModuleKind.MODULE_UMD
        }
    }
}

fun KotlinMultiplatformExtension.configureWasm() {
    configureWasmForKtor()
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }
}

fun KotlinMultiplatformExtension.configureWasmForKtor() {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }
}

fun KotlinMultiplatformExtension.configureNative() {
    linuxArm64()
    configureNativeForOpenAi()
}

fun KotlinMultiplatformExtension.configureNativeForOpenAi() {
    if (HostManager.hostIsMac) {
        configureIosForGemini()
        // According to https://kotlinlang.org/docs/native-target-support.html
        // Tier 1
        macosX64()
        macosArm64()
        // Tier 2
        watchosSimulatorArm64()
        watchosX64()
        watchosArm32()
        watchosArm64()
        tvosSimulatorArm64()
        tvosX64()
        tvosArm64()
        // Tier 3
        watchosDeviceArm64()
    }

    // Tier 2
    linuxX64()

    // Tier 3
    mingwX64()
//    androidNativeArm32()
//    androidNativeArm64()
//    androidNativeX86()
//    androidNativeX64()

    // setup tests running in RELEASE mode
    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.test(listOf(NativeBuildType.RELEASE))
    }
    targets.withType<KotlinNativeTargetWithTests<*>>().configureEach {
        testRuns.create("releaseTest") {
            setExecutionSourceFrom(binaries.getTest(NativeBuildType.RELEASE))
        }
    }
}

fun KotlinMultiplatformExtension.configureIosForGemini() {
    if (HostManager.hostIsMac) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
    }
}
