# Use Cases & Recipes

Real-world patterns and code examples for common PlatformSense scenarios. Each recipe includes the full code and explains when to use it.

---

## Table of Contents

- [Adaptive Animations](#1-adaptive-animations)
- [Network-Adaptive Content](#2-network-adaptive-content)
- [Biometric Feature Gating](#3-biometric-feature-gating)
- [Layout Adaptation by Device Class](#4-layout-adaptation-by-device-class)
- [Compose Multiplatform Integration](#5-compose-multiplatform-integration)
- [Offline-First Patterns](#6-offline-first-patterns)
- [Locale-Aware Formatting](#7-locale-aware-formatting)
- [Thermal Throttling](#8-thermal-throttling)
- [Combined Signal Decisions](#9-combined-signal-decisions)

---

## 1. Adaptive Animations

**Scenario:** Disable or reduce animations when the device is in low power mode to save battery.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.domain.PowerState

fun shouldEnableAnimations(): Boolean {
    val power = PlatformSense.environment().powerInfo
    return power.status != PowerState.LOW_POWER
}

// Usage in your UI layer:
if (shouldEnableAnimations()) {
    playFancyTransition()
} else {
    skipToFinalState()
}
```

**When to use:** Any app with animations, transitions, or particle effects that consume CPU/GPU.

<!-- SCREENSHOT: Sample app showing reduced animations in low power mode -->

---

## 2. Network-Adaptive Content

**Scenario:** Reduce image quality or defer large downloads on metered networks.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense

enum class ImageQuality { HIGH, MEDIUM, LOW }

fun selectImageQuality(): ImageQuality {
    val network = PlatformSense.environment().networkState
    return when {
        !network.isConnected       -> ImageQuality.LOW    // Offline — use cached/low-res
        network.isMetered          -> ImageQuality.MEDIUM  // Cellular — save data
        network.isRoaming          -> ImageQuality.LOW     // Roaming — minimize data
        else                       -> ImageQuality.HIGH    // WiFi — full quality
    }
}
```

**Advanced: Defer large downloads**

```kotlin
fun shouldAutoDownload(fileSizeMb: Int): Boolean {
    val network = PlatformSense.environment().networkState
    return when {
        !network.isConnected -> false
        network.isMetered && fileSizeMb > 10 -> false  // Don't auto-download >10MB on cellular
        network.isRoaming -> false                      // Never auto-download on roaming
        else -> true
    }
}
```

<!-- SCREENSHOT: Sample app showing image quality adaptation based on network type -->

---

## 3. Biometric Feature Gating

**Scenario:** Show biometric login only when the device supports it and the user has enrolled.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.domain.BiometricStatus
import io.github.anandkumarkparmar.platformsense.domain.BiometricType

fun configureBiometricLogin() {
    val bio = PlatformSense.capabilities().biometric

    when (bio.status) {
        BiometricStatus.READY -> {
            // Show biometric login button with appropriate icon
            val icon = when (bio.type) {
                BiometricType.FACE        -> FaceIdIcon
                BiometricType.FINGERPRINT -> FingerprintIcon
                else                      -> GenericBiometricIcon
            }
            showBiometricButton(icon)
        }

        BiometricStatus.NOT_ENROLLED -> {
            // Show prompt to enroll biometrics in settings
            showEnrollBiometricsPrompt()
        }

        BiometricStatus.UNAVAILABLE -> {
            // Biometric hardware exists but is temporarily unavailable (e.g. locked out)
            showPasswordLoginOnly()
        }

        BiometricStatus.NOT_SUPPORTED -> {
            // Device has no biometric hardware — don't show biometric options at all
            showPasswordLoginOnly()
        }
    }
}
```

<!-- SCREENSHOT: Sample app showing biometric login gate with Face ID/Touch ID icons -->

---

## 4. Layout Adaptation by Device Class

**Scenario:** Use different layouts based on the device form factor.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.domain.DeviceClass

fun selectLayout(): LayoutType {
    return when (PlatformSense.environment().deviceInfo.deviceClass) {
        DeviceClass.PHONE   -> LayoutType.SINGLE_COLUMN
        DeviceClass.TABLET  -> LayoutType.MULTI_PANE
        DeviceClass.DESKTOP -> LayoutType.WIDE_MULTI_PANE
        DeviceClass.TV      -> LayoutType.LEANBACK
        DeviceClass.UNKNOWN -> LayoutType.SINGLE_COLUMN  // Safe fallback
    }
}
```

---

## 5. Compose Multiplatform Integration

**Scenario:** Use `environmentFlow` in a Compose Multiplatform ViewModel or directly in a Composable.

### In a ViewModel

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.domain.EnvironmentSnapshot
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EnvironmentViewModel : ViewModel() {

    val environment: StateFlow<EnvironmentSnapshot> =
        PlatformSense.environmentFlow
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = PlatformSense.environment()
            )
}
```

### In a Composable (directly)

```kotlin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.anandkumarkparmar.platformsense.core.PlatformSense

@Composable
fun EnvironmentAwareScreen() {
    val env by PlatformSense.environmentFlow.collectAsState(
        initial = PlatformSense.environment()
    )

    // UI automatically recomposes when environment changes
    Text("Network: ${env.networkState.type}")
    Text("Battery: ${env.powerInfo.batteryLevel?.let { "${(it * 100).toInt()}%" } ?: "Unknown"}")
    Text("Device: ${env.deviceInfo.deviceClass}")
    Text("Locale: ${env.localeInfo.languageTag}")
    Text("Timezone: ${env.timezoneInfo.id}")
}
```

### Environment-Aware Composable Wrapper

```kotlin
@Composable
fun <T> EnvironmentAware(
    selector: (EnvironmentSnapshot) -> T,
    content: @Composable (T) -> Unit
) {
    val env by PlatformSense.environmentFlow.collectAsState(
        initial = PlatformSense.environment()
    )
    content(selector(env))
}

// Usage:
@Composable
fun MyScreen() {
    EnvironmentAware(selector = { it.powerInfo.status }) { powerState ->
        if (powerState == PowerState.LOW_POWER) {
            LowPowerBanner()
        }
    }
}
```

---

## 6. Offline-First Patterns

**Scenario:** React to connectivity changes and switch between online and offline modes.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged

// Observe connectivity changes
val isOnlineFlow = PlatformSense.environmentFlow
    .map { it.networkState.isConnected }
    .distinctUntilChanged()

// In a CoroutineScope:
isOnlineFlow.collect { isOnline ->
    if (isOnline) {
        syncPendingData()
        showOnlineBanner()
    } else {
        showOfflineBanner()
        switchToLocalCache()
    }
}
```

**Sync strategy based on network type:**

```kotlin
fun determineSyncStrategy(): SyncStrategy {
    val network = PlatformSense.environment().networkState
    return when {
        !network.isConnected -> SyncStrategy.QUEUE_ONLY      // Store locally
        network.isMetered    -> SyncStrategy.ESSENTIAL_ONLY   // Sync critical data only
        else                 -> SyncStrategy.FULL_SYNC        // Sync everything
    }
}
```

---

## 7. Locale-Aware Formatting

**Scenario:** Adapt content presentation based on the user's locale settings.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense

fun formatTime(hour: Int, minute: Int): String {
    val locale = PlatformSense.environment().localeInfo
    return if (locale.is24HourFormat) {
        String.format("%02d:%02d", hour, minute)
    } else {
        val period = if (hour < 12) "AM" else "PM"
        val displayHour = if (hour % 12 == 0) 12 else hour % 12
        String.format("%d:%02d %s", displayHour, minute, period)
    }
}

fun selectContentLanguage(): String {
    val locale = PlatformSense.environment().localeInfo
    val supportedLanguages = setOf("en", "ja", "de", "fr", "es")
    return if (locale.language in supportedLanguages) {
        locale.language
    } else {
        "en" // Fallback to English
    }
}
```

---

## 8. Thermal Throttling

**Scenario:** Reduce heavy operations when the device is overheating.

```kotlin
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.domain.ThermalState

fun configurePerformance() {
    val thermal = PlatformSense.environment().powerInfo.thermalState

    when (thermal) {
        ThermalState.NORMAL -> {
            enableFullParticleEffects()
            setRenderQuality(Quality.HIGH)
        }
        ThermalState.FAIR -> {
            reduceParticleCount(by = 0.5f)
            setRenderQuality(Quality.MEDIUM)
        }
        ThermalState.SERIOUS -> {
            disableParticleEffects()
            setRenderQuality(Quality.LOW)
            pauseBackgroundProcessing()
        }
        ThermalState.CRITICAL -> {
            disableAllNonEssentialWork()
            setRenderQuality(Quality.MINIMUM)
            showThermalWarning()
        }
        ThermalState.UNKNOWN -> {
            // Graceful fallback — proceed normally
            setRenderQuality(Quality.HIGH)
        }
    }
}
```

> [!NOTE]
> Thermal state is currently fully supported on **iOS** (via `ProcessInfo.thermalState`). On **Android**, thermal state returns `UNKNOWN` in the current version. See [Platform Support](platform-support.md) for details.

---

## 9. Combined Signal Decisions

**Scenario:** Make complex decisions based on multiple environment signals at once.

### Adaptive Media Loading

```kotlin
data class MediaConfig(
    val imageQuality: ImageQuality,
    val autoplayVideo: Boolean,
    val prefetchEnabled: Boolean
)

fun determineMediaConfig(): MediaConfig {
    val env = PlatformSense.environment()
    val network = env.networkState
    val power = env.powerInfo

    val isConstrained = network.isMetered
        || power.status == PowerState.LOW_POWER
        || power.thermalState == ThermalState.SERIOUS
        || power.thermalState == ThermalState.CRITICAL

    return MediaConfig(
        imageQuality = if (isConstrained) ImageQuality.LOW else ImageQuality.HIGH,
        autoplayVideo = !isConstrained && network.isConnected,
        prefetchEnabled = !isConstrained && network.type == NetworkType.WIFI
    )
}
```

### Feature Availability Matrix

```kotlin
data class FeatureFlags(
    val biometricLoginEnabled: Boolean,
    val offlineModeEnabled: Boolean,
    val deepAnimationsEnabled: Boolean,
    val hqMediaEnabled: Boolean
)

fun resolveFeatureFlags(): FeatureFlags {
    val env = PlatformSense.environment()
    val caps = PlatformSense.capabilities()

    return FeatureFlags(
        biometricLoginEnabled = caps.biometric.status == BiometricStatus.READY,
        offlineModeEnabled = !env.networkState.isConnected,
        deepAnimationsEnabled = env.powerInfo.status == PowerState.NORMAL
            && env.powerInfo.thermalState != ThermalState.SERIOUS,
        hqMediaEnabled = env.networkState.isConnected
            && !env.networkState.isMetered
            && env.powerInfo.status == PowerState.NORMAL
    )
}
```

---

## See Also

- **[API Reference](api-reference.md)** — Full property and enum reference
- **[Testing Guide](testing-guide.md)** — How to test the patterns above with fake providers
- **[Platform Support](platform-support.md)** — Which signals are available on which platform
