import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
}

val commonAppDir = "commonApp"
val androidAppDir = "androidApp"

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    sourceSets {
        commonMain.get().kotlin.setSrcDirs(listOf("$commonAppDir/commonMain/kotlin"))
        commonMain.get().resources.setSrcDirs(listOf("$commonAppDir/commonMain/resources"))
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(project(":platformsense-core"))
            implementation(project(":platformsense-testing"))
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies { implementation(kotlin("test")) }
        androidMain.get().kotlin.setSrcDirs(listOf("$androidAppDir/kotlin", "$commonAppDir/androidMain/kotlin"))
        androidMain.get().resources.setSrcDirs(listOf("$androidAppDir/resources"))
        androidMain.dependencies {
            implementation(project(":platformsense-android"))
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "io.platformsense.sample"
    compileSdk = libs.versions.compileSdk.get().toInt()
    sourceSets.getByName("main").manifest.srcFile("$androidAppDir/AndroidManifest.xml")
    defaultConfig {
        applicationId = "io.platformsense.sample"
        minSdk = libs.versions.minSdkSample.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = libs.versions.platformsense.get()
    }
    buildTypes.getByName("release") {
        isMinifyEnabled = false
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures.compose = true
}
