plugins {
    kotlin("multiplatform")
}

group = "io.platformsense"

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "platformsense-platform-ios"
            isStatic = true
        }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(project(":platformsense-core"))
            implementation(project(":platformsense-domain"))
        }
    }
}
