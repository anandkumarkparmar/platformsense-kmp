plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

group = "io.platformsense"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":platformsense-core"))
            implementation(project(":platformsense-domain"))
        }
        androidMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
        }
    }
}

android {
    namespace = "io.platformsense.platform.android"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
