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
    ":platformsense-domain",
    ":platformsense-core",
    ":platformsense-platform-android",
    ":platformsense-platform-ios",
    ":platformsense-testing"
)
