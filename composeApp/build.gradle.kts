import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    jvmToolchain(17)

    android {
        namespace = "dev.logickoder.starter"
        compileSdk = 36
        minSdk = 26
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            // Compose — explicit (implicit compose.* accessors deprecated)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.material.icons)

            // Lifecycle
            implementation(libs.lifecycle.runtime)
            implementation(libs.lifecycle.viewmodel)

            // Navigation 3
            implementation(libs.navigation)

            // Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            // RetroStash — annotation-driven HTTP caching over Ktor
            implementation(libs.retrostash.core)
            implementation(libs.retrostash.ktor)

            // Room
            implementation(libs.room.runtime)

            // Koin — DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Kotlinx
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.coroutines.core)
        }

        androidMain.dependencies {
            implementation(libs.datastore.preferences)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.coroutines.android)
            implementation(libs.core)
            implementation(libs.room.sqlite)
        }

        iosMain.dependencies {
            implementation(libs.datastore.preferences)
            implementation(libs.ktor.client.darwin)
            implementation(libs.room.sqlite)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.datastore.preferences)
                implementation(compose.desktop.currentOs)
                implementation(libs.room.sqlite)
                implementation(libs.ktor.client.cio)
                implementation(libs.coroutines.swing)
                implementation(libs.slf4j.simple)
            }
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.room.sqlite.web)
            implementation(npm("sqlite-wasm-worker", layout.projectDirectory.dir("worker").asFile))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

room3 {
    schemaDirectory("$projectDir/schemas")
}

// KSP annotation processors for Room on all supported targets
dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspDesktop", libs.room.compiler)
    add("kspWasmJs", libs.room.compiler)
}
