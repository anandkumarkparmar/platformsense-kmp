# Platform Support

Detailed breakdown of platform-specific implementations, native APIs used, and known limitations.

---

## Support Matrix

| Feature | Android | iOS | Desktop | Web |
|---------|---------|-----|---------|-----|
| **Network type** | ✅ `ConnectivityManager` | ✅ `NWPathMonitor` | 🔜 Planned | 🔜 Planned |
| **Network connected** | ✅ | ✅ | 🔜 | 🔜 |
| **Network metered** | ✅ | ✅ | 🔜 | 🔜 |
| **Network roaming** | ✅ | ⚠️ Returns `false` | 🔜 | 🔜 |
| **Power state** | ✅ `PowerManager` | ✅ `ProcessInfo` | 🔜 | 🔜 |
| **Battery level** | ✅ `BatteryManager` | ✅ `UIDevice` | 🔜 | 🔜 |
| **Is charging** | ✅ | ✅ `UIDevice` | 🔜 | 🔜 |
| **Thermal state** | ⚠️ Returns `UNKNOWN` | ✅ `ProcessInfo` | 🔜 | 🔜 |
| **Device class** | ✅ Screen metrics | ✅ `UIDevice.model` | 🔜 | 🔜 |
| **OS name/version** | ✅ `Build` | ✅ `UIDevice` | 🔜 | 🔜 |
| **Manufacturer/model** | ✅ `Build` | ✅ `UIDevice` | 🔜 | 🔜 |
| **Locale** | ✅ `Locale` | ✅ `NSLocale` | 🔜 | 🔜 |
| **24-hour format** | ✅ `DateFormat` | ✅ `NSLocale` | 🔜 | 🔜 |
| **Timezone** | ✅ `TimeZone` | ✅ `NSTimeZone` | 🔜 | 🔜 |
| **Biometric status** | ✅ `BiometricManager` | ✅ `LAContext` | 🔜 | 🔜 |
| **Biometric type** | ✅ | ✅ | 🔜 | 🔜 |
| **Reactive flows** | ✅ | ✅ | 🔜 | 🔜 |

**Legend:** ✅ Supported | ⚠️ Partial | 🔜 Planned | ❌ Not applicable

---

## Android

**Minimum API:** 23 (Android 6.0 Marshmallow)

### Initialization

```kotlin
PlatformSense.initialize(AndroidPlatformSenseWiring(applicationContext))
```

### Native APIs Used

| Provider | Primary API | Notes |
|----------|-------------|-------|
| `AndroidNetworkProvider` | `ConnectivityManager`, `NetworkCallback` | Reactive via `NetworkCallback`; requires `ACCESS_NETWORK_STATE` permission |
| `AndroidPowerProvider` | `PowerManager`, `BatteryManager` | `isPowerSaveMode` for low-power detection; battery level via `BatteryManager` |
| `AndroidDeviceProvider` | `Build`, `Configuration`, screen metrics | `deviceClass` determined by screen width (< 600dp = Phone, ≥ 600dp = Tablet) |
| `AndroidLocaleProvider` | `Locale`, `DateFormat` | `is24HourFormat` via `android.text.format.DateFormat` |
| `AndroidTimezoneProvider` | `java.util.TimeZone` | IANA timezone ID and offset |
| `AndroidBiometricProvider` | `BiometricManager` | `canAuthenticate(BIOMETRIC_STRONG)` for status detection |

### Required Permissions

```xml
<!-- Required for NetworkProvider -->
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

No other runtime permissions are required.

### ProGuard / R8

PlatformSense does not require special ProGuard rules. All public API classes use standard Kotlin data classes and enums that survive minification.

<!-- SCREENSHOT: Sample app running on Android showing all environment signals -->

---

## iOS

**Minimum Deployment Target:** iOS 14+

### Initialization

From Swift code:

```swift
import shared // or your KMP framework module name
IosPlatformSenseWiringKt.initializePlatformSense()
```

### Native APIs Used

| Provider | Primary API | Framework | Notes |
|----------|-------------|-----------|-------|
| `IosNetworkProvider` | `NWPathMonitor` | Network | Monitors network path changes; detects WiFi vs cellular |
| `IosPowerProvider` | `UIDevice.batteryLevel`, `ProcessInfo.isLowPowerModeEnabled`, `ProcessInfo.thermalState` | UIKit, Foundation | Battery monitoring requires `UIDevice.current.isBatteryMonitoringEnabled = true` |
| `IosDeviceProvider` | `UIDevice.model`, `UIDevice.systemName`, `UIDevice.systemVersion` | UIKit | Device class inferred from model string |
| `IosLocaleProvider` | `NSLocale.current` | Foundation | Language tag, country, 24-hour detection |
| `IosTimezoneProvider` | `NSTimeZone.localTimeZone` | Foundation | IANA timezone ID and offset |
| `IosBiometricProvider` | `LAContext.canEvaluatePolicy(.deviceOwnerAuthenticationWithBiometrics)` | LocalAuthentication | Detects Face ID vs Touch ID via `biometryType` |

### Known Limitations

| Signal | Limitation |
|--------|------------|
| `isRoaming` | Always returns `false` — iOS does not expose roaming state via public API |
| `manufacturer` | Always returns `"Apple"` |
| Battery monitoring | Must be explicitly enabled; the iOS provider handles this internally |

### No Special Entitlements Required

PlatformSense does not require any special entitlements or Info.plist keys.

<!-- SCREENSHOT: Sample app running on iOS showing all environment signals -->

---

## Desktop (Planned)

> [!NOTE]
> Desktop support is planned. The following outlines the expected approach.

### Expected APIs

| Provider | Expected API | Notes |
|----------|-------------|-------|
| `NetworkProvider` | `java.net.NetworkInterface` | Detect connectivity and type |
| `PowerProvider` | OS-specific (JNA/JNI) or `UNKNOWN` | Battery info varies by OS |
| `DeviceProvider` | `System.getProperty()` | Always `DeviceClass.DESKTOP` |
| `LocaleProvider` | `java.util.Locale` | Similar to Android |
| `TimezoneProvider` | `java.util.TimeZone` | Same as Android |
| `BiometricProvider` | `NOT_SUPPORTED` | Most desktops lack biometric APIs |

---

## Web (Planned)

> [!NOTE]
> Web support is planned. The following outlines the expected approach.

### Expected APIs

| Provider | Expected API | Notes |
|----------|-------------|-------|
| `NetworkProvider` | `navigator.onLine`, `navigator.connection` | Connection API is experimental |
| `PowerProvider` | Battery Status API | Limited browser support |
| `DeviceProvider` | `navigator.userAgent`, media queries | Best-effort device class detection |
| `LocaleProvider` | `navigator.language` | BCP-47 tag |
| `TimezoneProvider` | `Intl.DateTimeFormat().resolvedOptions().timeZone` | Standard across browsers |
| `BiometricProvider` | Web Authentication API | FIDO2/WebAuthn support |

---

## Unsupported Signals and Fallback Behavior

PlatformSense is designed to be **platform-safe**. When a signal cannot be determined on a given platform, it returns a safe default rather than crashing:

| Default Value | Used When |
|---------------|-----------|
| `NetworkType.UNKNOWN` | Network type cannot be determined |
| `PowerState.UNKNOWN` | Power state is unavailable |
| `ThermalState.UNKNOWN` | Thermal state is not supported (e.g. Android) |
| `DeviceClass.UNKNOWN` | Device form factor cannot be inferred |
| `BiometricStatus.NOT_SUPPORTED` | Platform lacks biometric hardware |
| `BiometricType.UNKNOWN` | Biometric type cannot be determined |
| `""` (empty string) | String properties when information unavailable |
| `null` | `batteryLevel` when unknown |
| `false` | Boolean properties when undetermined |

> [!IMPORTANT]
> Always write code that handles `UNKNOWN` values gracefully. Use `when` with an `else` branch or fallback logic.

```kotlin
when (env.networkState.type) {
    NetworkType.WIFI     -> highQualityMode()
    NetworkType.CELLULAR -> reducedQualityMode()
    else                 -> defaultMode() // handles UNKNOWN, NONE, ETHERNET
}
```

---

## See Also

- **[Getting Started](getting-started.md)** — Per-platform setup instructions
- **[Architecture](architecture.md)** — How providers connect to the core
- **[Roadmap](roadmap.md)** — Timeline for Desktop and Web support
