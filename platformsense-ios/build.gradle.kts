plugins {
    alias(libs.plugins.kotlin.multiplatform)
    `maven-publish`
}

version = libs.versions.platformsense.get()

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "PlatformSenseIos"
            isStatic = true
            export(project(":platformsense-core"))
        }
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":platformsense-core"))
        }
    }
}
