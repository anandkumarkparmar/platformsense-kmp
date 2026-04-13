plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

android {
    namespace = "io.github.anandkumarkparmar.platformsense.sample"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "io.github.anandkumarkparmar.platformsense.sample"
        minSdk = libs.versions.minSdkSample.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "dev"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures.compose = true
    lint {
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(project(":sample:commonApp"))
    implementation(project(":platformsense-android"))

    implementation(libs.androidx.activity.compose)
    implementation(compose.material3)
}
