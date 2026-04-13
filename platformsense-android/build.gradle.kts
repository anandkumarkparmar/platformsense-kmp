plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.binary.compatibility.validator)
    alias(libs.plugins.dokka)
    `maven-publish`
}

version = libs.versions.platformsense.get()

kotlin {
    explicitApi()

    android {
        namespace = "io.github.anandkumarkparmar.platformsense.platform.android"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {
            api(project(":platformsense-core"))
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}
