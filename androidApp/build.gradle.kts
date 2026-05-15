plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

val syncComposeAppResources by tasks.registering(Copy::class) {
    dependsOn(":composeApp:prepareComposeResourcesTaskForCommonMain")
    from(
        project(":composeApp")
            .layout
            .buildDirectory
            .dir("generated/compose/resourceGenerator/preparedResources/commonMain/composeResources"),
    )
    into(layout.buildDirectory.dir("generated/composeAppAssets/composeResources/com.hng14.energyiq"))
}

android {
    namespace = "com.hng14.energyiq.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hng14.energyiq"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.named("main") {
        assets.srcDir("$buildDir/generated/composeAppAssets")
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.activity.compose)
    implementation(libs.koin.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)
}

tasks.matching { it.name.startsWith("merge") && it.name.endsWith("Assets") }.configureEach {
    dependsOn(syncComposeAppResources)
}
