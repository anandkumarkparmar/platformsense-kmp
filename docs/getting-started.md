# Getting Started

This guide walks you through adding PlatformSense to your Kotlin Multiplatform project and making your first environment query.

---

## Prerequisites

| Requirement | Minimum Version |
|-------------|-----------------|
| Kotlin | 2.0+ |
| Gradle | 8.0+ |
| Android Gradle Plugin | 8.0+ |
| Android `minSdk` | 23 (Android 6.0) |
| Xcode (iOS) | 15+ |

Your project must already be configured as a **Kotlin Multiplatform** project. If you're starting from scratch, see the [official KMP setup guide](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-getting-started.html).

---

## 1. Add the Repository

PlatformSense is distributed via [JitPack](https://jitpack.io). Add the JitPack repository to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

> [!NOTE]
> If you use a `buildscript` block instead, add the maven repository there as well.

---

## 2. Add Dependencies

In your module's `build.gradle.kts`, add the modules you need:

### Core (required)

```kotlin
commonMain.dependencies {
    implementation("io.github.anandkumarkparmar:platformsense-core:0.1.0")
}
```

### Android Platform (required for Android targets)

```kotlin
androidMain.dependencies {
    implementation("io.github.anandkumarkparmar:platformsense-android:0.1.0")
}
```

### iOS Platform (required for iOS targets)

```kotlin
iosMain.dependencies {
    implementation("io.github.anandkumarkparmar:platformsense-ios:0.1.0")
}
```

### Testing (optional, for unit tests)

```kotlin
commonTest.dependencies {
    implementation("io.github.anandkumarkparmar:platformsense-testing:0.1.0")
}
```

### Dependency Summary

| Module | Purpose | Required? |
|--------|---------|-----------|
| `platformsense-core` | Domain models, facade, repositories | ✅ Always |
| `platformsense-android` | Android provider implementations | ✅ Android targets |
| `platformsense-ios` | iOS provider implementations | ✅ iOS targets |
| `platformsense-testing` | Fake providers & test rule | Optional (tests) |

---

## 3. Platform Initialization

PlatformSense must be initialized **once** at app startup before any calls to `PlatformSense.environment()` or `PlatformSense.capabilities()`.

### Android

Initialize in your `Application` class (recommended) or main `Activity`:

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.platform.android.AndroidPlatformSenseWiring

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformSense.initialize(AndroidPlatformSenseWiring(this))
    }
}
```

> [!IMPORTANT]
> Pass the **Application context** (`this` from `Application.onCreate()`), not an Activity context. This ensures providers survive Activity recreation.

Don't forget to register your `Application` class in `AndroidManifest.xml`:

```xml
<application
    android:name=".MyApplication"
    ... >
```

### iOS

Initialize from your Swift entry point. PlatformSense ships a top-level helper function for Swift interop:

**SwiftUI (`App` struct):**

```swift
import shared // or your KMP framework name

@main
struct MyApp: App {
    init() {
        IosPlatformSenseWiringKt.initializePlatformSense()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

**UIKit (`AppDelegate`):**

```swift
import shared

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        IosPlatformSenseWiringKt.initializePlatformSense()
        return true
    }
}
```

### Desktop & Web

> [!NOTE]
> Desktop and Web platform providers are **coming soon**. See the [Roadmap](roadmap.md) for timeline details. You can still use `platformsense-core` domain models and the testing module on these targets.

---

## 4. First Usage

Once initialized, you can query environment and capabilities from anywhere in your shared code:

### Snapshot (one-shot query)

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense

val env = PlatformSense.environment()
println("Network: ${env.networkInfo.type}")          // WIFI, CELLULAR, NONE, ...
println("Battery: ${env.powerInfo.batteryLevel}")    // 0.0..1.0 or null
println("Device:  ${env.deviceInfo.deviceClass}")    // PHONE, TABLET, ...
println("Locale:  ${env.localeInfo.languageTag}")    // "en-US", "ja-JP", ...
println("TZ:      ${env.timezoneInfo.id}")           // "America/New_York", ...

val caps = PlatformSense.capabilities()
println("Biometric: ${caps.biometric.status}")       // READY, NOT_ENROLLED, ...
```

### Reactive (Flow-based)

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import kotlinx.coroutines.flow.collect

// In a CoroutineScope (e.g. ViewModel, lifecycleScope):
PlatformSense.environmentFlow.collect { env ->
    // Called whenever network, power, locale, etc. changes
    updateUI(env)
}
```

---

## 5. Verify Installation

You can check whether PlatformSense has been initialized:

```kotlin
if (PlatformSense.isInitialized()) {
    val env = PlatformSense.environment()
    // Safe to use
} else {
    // Not yet initialized — call PlatformSense.initialize() first
}
```

If you call `environment()` or `capabilities()` before initialization, PlatformSense throws an `IllegalStateException` with a clear error message telling you to call `initialize()` first.

---

## Sample App

The repository includes a working sample app that demonstrates all features:

```bash
# Run the Android sample on a connected device or emulator
./gradlew :sample:androidApp:installDebug

# Run the iOS sample in the simulator (open in Xcode, then build & run)
open sample/iosApp/iosApp.xcodeproj
```

<!-- SCREENSHOT: Sample app dashboard showing all environment signals (network, power, device, locale, timezone, biometric) on Android -->

<!-- SCREENSHOT: Sample app dashboard showing all environment signals on iOS -->

See the [sample/README.md](../sample/README.md) for more details.

---

## Next Steps

- **[API Reference](api-reference.md)** — Full reference for all domain models and the public API
- **[Use Cases & Recipes](use-cases-and-recipes.md)** — Real-world patterns and code examples
- **[Testing Guide](testing-guide.md)** — How to test with fake providers
- **[Architecture](architecture.md)** — Understand the internal design
