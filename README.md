# PlatformSense

Kotlin Multiplatform library for sensing device environment and capabilities.

## Features

- **Network State Detection** - WiFi, Cellular, Metered, None
- **Power State Monitoring** - Normal, Low Power, Charging
- **Biometric Capability Detection** - Check if biometric auth is available
- **Device Class Identification** - Phone, Tablet, Desktop, TV
- **Locale & Timezone Information** - Current locale and timezone
- **Reactive Updates** - Kotlin Flow support for real-time changes

## Installation

Add JitPack repository to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add dependencies to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Core library (required)
    implementation("com.github.user.platformsense-kmp:platformsense-core:0.1.0")
    
    // Android platform (required for Android)
    implementation("com.github.user.platformsense-kmp:platformsense-android:0.1.0")
    
    // Testing utilities (optional)
    testImplementation("com.github.user.platformsense-kmp:platformsense-testing:0.1.0")
}
```

## Android Setup

### Permissions

Add to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.USE_BIOMETRIC" />
```

### Initialization

Initialize in your `Application` class or main `Activity`:

```kotlin
import io.platformsense.core.PlatformSense
import io.platformsense.platform.android.AndroidPlatformSenseWiring

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformSense.initialize(AndroidPlatformSenseWiring(this))
    }
}
```

## Usage

### Snapshot Queries

```kotlin
// Get current environment
val environment = PlatformSense.environment()
println("Network: ${environment.networkType}")
println("Power: ${environment.powerState}")
println("Device: ${environment.deviceClass}")
println("Locale: ${environment.locale}")
println("Timezone: ${environment.timezone}")

// Get current capabilities
val capabilities = PlatformSense.capabilities()
println("Biometric available: ${capabilities.biometric.isAvailable}")
```

### Reactive Updates

```kotlin
// Observe environment changes
PlatformSense.environmentFlow.collect { snapshot ->
    when (snapshot.powerState) {
        PowerState.LOW_POWER -> reduceAnimations()
        PowerState.CHARGING -> enableFullFeatures()
        else -> { }
    }
    
    when (snapshot.networkType) {
        NetworkType.METERED -> reduceDataUsage()
        NetworkType.WIFI -> enableHighQuality()
        NetworkType.NONE -> showOfflineMode()
        else -> { }
    }
}
```

## Testing

Use the testing module for unit tests:

```kotlin
import io.platformsense.testing.PlatformSenseTestRule
import io.platformsense.testing.fake.FakePlatformSenseWiring

class MyTest {
    private val testRule = PlatformSenseTestRule()
    
    @Before
    fun setup() {
        testRule.install()
    }
    
    @After
    fun teardown() {
        testRule.uninstall()
    }
    
    @Test
    fun testLowPowerMode() {
        // Configure fake provider
        testRule.wiring.powerProvider.currentValue = PowerState.LOW_POWER
        
        // Test your code
        val env = PlatformSense.environment()
        assertEquals(PowerState.LOW_POWER, env.powerState)
    }
}
```

## Platform Support

| Platform | Status | Min Version |
|----------|--------|-------------|
| Android | Supported | API 23+ (Android 6.0) |
| iOS | Coming Soon | - |
| Desktop | Coming Soon | - |

## Requirements

- Kotlin 2.1.0+
- Gradle 8.10+
- Java 17+
- Android: Compile SDK 36, Target SDK 36, Min SDK 23

## License

```
   Copyright 2026 Anand K Parmar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
