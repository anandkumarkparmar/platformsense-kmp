rootProject.name = "platformsense-kmp"

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

include(
    ":platformsense-core",
    ":platformsense-android",
    ":platformsense-ios",
    ":platformsense-testing",
    ":sample:commonApp",
    ":sample:androidApp",
)
