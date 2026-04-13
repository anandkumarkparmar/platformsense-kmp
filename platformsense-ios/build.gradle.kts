plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.binary.compatibility.validator)
    `maven-publish`
}

version = libs.versions.platformsense.get()

apiValidation {
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}

kotlin {
    explicitApi()
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
