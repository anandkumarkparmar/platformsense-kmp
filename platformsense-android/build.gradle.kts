plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    `maven-publish`
}

version = libs.versions.platformsense.get()

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions { jvmTarget = libs.versions.jvmTarget.get() }
        }
    }
    sourceSets {
        androidMain.dependencies {
            api(project(":platformsense-core"))
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}

android {
    namespace = "io.platformsense.platform.android"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.minSdk.get().toInt() }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
