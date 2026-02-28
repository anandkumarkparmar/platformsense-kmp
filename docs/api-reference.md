# API Reference

Complete reference for the PlatformSense public API surface, domain models, and enums.

---

## Table of Contents

- [PlatformSense Facade](#platformsense-facade)
- [Environment Models](#environment-models)
  - [EnvironmentSnapshot](#environmentsnapshot)
  - [NetworkState](#networkstate)
  - [PowerInfo](#powerinfo)
  - [DeviceInfo](#deviceinfo)
  - [LocaleInfo](#localeinfo)
  - [TimezoneInfo](#timezoneinfo)
- [Capability Models](#capability-models)
  - [CapabilitiesSnapshot](#capabilitiessnapshot)
  - [BiometricCapability](#biometriccapability)
- [Core Interfaces](#core-interfaces)
  - [PlatformSenseWiring](#platformsensewiring)
  - [EnvironmentRepository](#environmentrepository)
  - [CapabilitiesRepository](#capabilitiesrepository)
- [Provider Interfaces](#provider-interfaces)

---

## PlatformSense Facade

`object PlatformSense` — The single entry point for all environment and capability queries.

| Method / Property | Return Type | Description |
|-------------------|-------------|-------------|
| `initialize(wiring: PlatformSenseWiring)` | `Unit` | Configure with a platform wiring (e.g. `AndroidPlatformSenseWiring`). Call once at startup. |
| `initialize(environmentRepository, capabilitiesRepository)` | `Unit` | Configure with explicit repositories. Lower-level alternative. |
| `isInitialized()` | `Boolean` | Returns `true` if `initialize()` has been called. |
| `environment()` | `EnvironmentSnapshot` | Returns the current environment snapshot. Throws if not initialized. |
| `environmentFlow` | `Flow<EnvironmentSnapshot>` | Emits whenever any environment signal changes. Throws if not initialized. |
| `capabilities()` | `CapabilitiesSnapshot` | Returns the current capabilities snapshot. Throws if not initialized. |
| `resetForTest()` | `Unit` | Clears state for test teardown. **Test-only.** |

**Package:** `io.github.anandkumarkparmar.platformsense.core`

### Usage

```kotlin
// Initialize once
PlatformSense.initialize(AndroidPlatformSenseWiring(context))

// Snapshot
val env = PlatformSense.environment()
val caps = PlatformSense.capabilities()

// Reactive
PlatformSense.environmentFlow.collect { env -> ... }
```

---

## Environment Models

### EnvironmentSnapshot

Immutable snapshot aggregating all environment signals into a single value object.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `networkState` | `NetworkState` | `NetworkState()` | Current network connectivity state |
| `powerInfo` | `PowerInfo` | `PowerInfo()` | Current power and battery information |
| `deviceInfo` | `DeviceInfo` | `DeviceInfo()` | Device identity and form factor |
| `localeInfo` | `LocaleInfo` | `LocaleInfo()` | Current locale and formatting preferences |
| `timezoneInfo` | `TimezoneInfo` | `TimezoneInfo()` | Current timezone information |

```kotlin
val env = PlatformSense.environment()
// Access individual signals:
env.networkState.type       // NetworkType
env.powerInfo.status        // PowerState
env.deviceInfo.deviceClass  // DeviceClass
env.localeInfo.languageTag  // String
env.timezoneInfo.id         // String
```

---

### NetworkState

Represents the current network connectivity state.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `type` | `NetworkType` | `UNKNOWN` | The type of network transport |
| `isConnected` | `Boolean` | `false` | Whether the device has an active network connection |
| `isMetered` | `Boolean` | `false` | Whether the active network is metered (e.g. cellular) |
| `isRoaming` | `Boolean` | `false` | Whether the active network is roaming |

#### NetworkType Enum

| Value | Description |
|-------|-------------|
| `WIFI` | Connected via Wi-Fi |
| `CELLULAR` | Connected via cellular (mobile) network |
| `ETHERNET` | Connected via Ethernet |
| `NONE` | No network connectivity |
| `UNKNOWN` | Network type could not be determined |

```kotlin
val net = PlatformSense.environment().networkState
if (net.isConnected && !net.isMetered) {
    // Safe to download large files
}
```

---

### PowerInfo

Represents the current device power and battery information.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `status` | `PowerState` | `UNKNOWN` | The overall power status |
| `batteryLevel` | `Float?` | `null` | Battery level as a percentage from `0.0` to `1.0`. `null` if unknown. |
| `isCharging` | `Boolean` | `false` | Whether the device is connected to power |
| `thermalState` | `ThermalState` | `UNKNOWN` | Current thermal state of the device |

#### PowerState Enum

| Value | Description |
|-------|-------------|
| `NORMAL` | Normal power state; not in power-saving mode |
| `LOW_POWER` | Device is in low power / power saver mode |
| `UNKNOWN` | Power status could not be determined |

#### ThermalState Enum

| Value | Description |
|-------|-------------|
| `NORMAL` | Normal operating temperature |
| `FAIR` | Elevated temperature; consider reducing non-essential work |
| `SERIOUS` | High temperature; reduce work to avoid throttling or shutdown |
| `CRITICAL` | Critical temperature; device is near shutdown |
| `UNKNOWN` | Thermal state could not be determined |

```kotlin
val power = PlatformSense.environment().powerInfo
when (power.status) {
    PowerState.LOW_POWER -> reduceBackgroundWork()
    PowerState.NORMAL    -> proceedNormally()
    PowerState.UNKNOWN   -> proceedNormally() // graceful fallback
}

when (power.thermalState) {
    ThermalState.SERIOUS, ThermalState.CRITICAL -> throttleHeavyOperations()
    else -> { /* normal operation */ }
}
```

---

### DeviceInfo

Represents the device identity and hardware form factor.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `deviceClass` | `DeviceClass` | `UNKNOWN` | The form factor of the device |
| `osName` | `String` | `""` | Operating system name (e.g. "Android", "iOS") |
| `osVersion` | `String` | `""` | OS version (e.g. "14", "17.4") |
| `manufacturer` | `String` | `""` | Device manufacturer (e.g. "Samsung", "Apple") |
| `model` | `String` | `""` | Device model (e.g. "SM-G998B", "iPhone 15 Pro") |

#### DeviceClass Enum

| Value | Description |
|-------|-------------|
| `PHONE` | Phone or similar small-form-factor device |
| `TABLET` | Tablet or similar large-form-factor device |
| `DESKTOP` | Desktop or laptop |
| `TV` | TV or set-top device |
| `UNKNOWN` | Device class could not be determined |

```kotlin
val device = PlatformSense.environment().deviceInfo
when (device.deviceClass) {
    DeviceClass.PHONE  -> useSingleColumnLayout()
    DeviceClass.TABLET -> useMultiPaneLayout()
    DeviceClass.TV     -> useLeanbackLayout()
    else               -> useSingleColumnLayout()
}
```

---

### LocaleInfo

Represents the current device locale and regional preferences.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `languageTag` | `String` | `""` | BCP-47 language tag (e.g. "en-US", "ja-JP") |
| `language` | `String` | `""` | ISO 639 language code (e.g. "en", "ja") |
| `country` | `String` | `""` | ISO 3166 country/region code (e.g. "US", "JP") |
| `is24HourFormat` | `Boolean` | `false` | Whether user prefers 24-hour time formatting |

```kotlin
val locale = PlatformSense.environment().localeInfo
println("Language: ${locale.languageTag}")  // "en-US"
println("24hr: ${locale.is24HourFormat}")   // true/false
```

---

### TimezoneInfo

Represents the current device timezone settings.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `id` | `String` | `""` | IANA timezone identifier (e.g. "America/New_York", "Asia/Tokyo") |
| `displayName` | `String` | `""` | Localized display name (e.g. "Eastern Standard Time") |
| `offsetMillis` | `Int` | `0` | Offset from UTC in milliseconds (accounts for DST) |

```kotlin
val tz = PlatformSense.environment().timezoneInfo
println("Timezone: ${tz.id}")            // "America/New_York"
println("Offset: ${tz.offsetMillis}ms")  // -18000000
```

---

## Capability Models

### CapabilitiesSnapshot

Immutable snapshot of device and runtime capabilities.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `biometric` | `BiometricCapability` | `BiometricCapability()` | Biometric authentication capability |
| `secureHardwareAvailable` | `Boolean` | `false` | Whether secure hardware (TEE, Secure Enclave) is available |
| `accessibilityAvailable` | `Boolean` | `false` | Whether accessibility features are enabled |

> [!NOTE]
> `secureHardwareAvailable` and `accessibilityAvailable` are placeholders for future provider implementations. They currently default to `false`.

```kotlin
val caps = PlatformSense.capabilities()
if (caps.biometric.status == BiometricStatus.READY) {
    showBiometricLogin()
}
```

---

### BiometricCapability

Immutable capability signal for biometric authentication support.

**Package:** `io.github.anandkumarkparmar.platformsense.domain`

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `status` | `BiometricStatus` | `NOT_SUPPORTED` | Current enrollment and availability status |
| `type` | `BiometricType` | `UNKNOWN` | Primary biometric type, if known |

#### BiometricStatus Enum

| Value | Description |
|-------|-------------|
| `READY` | Hardware present and user has enrolled biometric templates. Ready to use. |
| `NOT_ENROLLED` | Hardware present, but user has not enrolled any templates |
| `UNAVAILABLE` | Hardware unavailable, locked out, or temporarily disabled |
| `NOT_SUPPORTED` | Device does not support biometric authentication |

#### BiometricType Enum

| Value | Description |
|-------|-------------|
| `FINGERPRINT` | Fingerprint or Touch ID sensor |
| `FACE` | Face recognition or Face ID |
| `IRIS` | Iris scanner |
| `MULTIPLE_OR_GENERIC` | Multiple types available, or type cannot be determined but biometrics are supported |
| `UNKNOWN` | Could not be determined |

```kotlin
val bio = PlatformSense.capabilities().biometric
when (bio.status) {
    BiometricStatus.READY        -> enableBiometricLogin()
    BiometricStatus.NOT_ENROLLED -> promptToEnrollBiometrics()
    BiometricStatus.UNAVAILABLE  -> showPasswordFallback()
    BiometricStatus.NOT_SUPPORTED -> hideBiometricOption()
}

// Check biometric type
if (bio.type == BiometricType.FACE) {
    showFaceIdIcon()
} else if (bio.type == BiometricType.FINGERPRINT) {
    showFingerprintIcon()
}
```

---

## Core Interfaces

### PlatformSenseWiring

Interface that platform modules implement to supply repositories.

**Package:** `io.github.anandkumarkparmar.platformsense.core`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `environmentRepository()` | `EnvironmentRepository` | Provides the environment repository with platform-specific providers |
| `capabilitiesRepository()` | `CapabilitiesRepository` | Provides the capabilities repository with platform-specific providers |

**Implementations:**
- `AndroidPlatformSenseWiring(context: Context)` — Android (package: `io.github.anandkumarkparmar.platformsense.platform.android`)
- `IosPlatformSenseWiring()` — iOS (package: `io.github.anandkumarkparmar.platformsense.platform.ios`)

---

### EnvironmentRepository

Aggregates environment providers into a single `EnvironmentSnapshot` and a reactive `Flow`.

**Package:** `io.github.anandkumarkparmar.platformsense.core`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `current()` | `EnvironmentSnapshot` | Returns the current snapshot by querying all providers. Caches the result. |
| `lastCached()` | `EnvironmentSnapshot?` | Returns the last cached snapshot, or `null` if none. |
| `flow()` | `Flow<EnvironmentSnapshot>` | Emits whenever any provider emits a new value. Updates cache on each emission. |

**Constructor** takes lambda factories for lazy provider resolution:

```kotlin
EnvironmentRepository(
    networkProvider  = { AndroidNetworkProvider(context) },
    powerProvider    = { AndroidPowerProvider(context) },
    deviceProvider   = { AndroidDeviceProvider(context) },
    localeProvider   = { AndroidLocaleProvider(context) },
    timezoneProvider = { AndroidTimezoneProvider(context) },
)
```

---

### CapabilitiesRepository

Aggregates capability providers into a single `CapabilitiesSnapshot`.

**Package:** `io.github.anandkumarkparmar.platformsense.core`

| Method | Return Type | Description |
|--------|-------------|-------------|
| `current()` | `CapabilitiesSnapshot` | Returns the current capabilities snapshot |

**Constructor** takes a lambda factory for the biometric provider:

```kotlin
CapabilitiesRepository(
    biometricProvider = { AndroidBiometricProvider(context) },
)
```

---

## Provider Interfaces

Each signal has a provider interface in `io.github.anandkumarkparmar.platformsense.core.provider`:

| Interface | Value Type | Description |
|-----------|-----------|-------------|
| `NetworkProvider` | `NetworkState` | Network connectivity state |
| `PowerProvider` | `PowerInfo` | Power and battery info |
| `DeviceProvider` | `DeviceInfo` | Device identity and form factor |
| `LocaleProvider` | `LocaleInfo` | Locale and formatting preferences |
| `TimezoneProvider` | `TimezoneInfo` | Timezone settings |
| `BiometricProvider` | `BiometricCapability` | Biometric capability signal |

Each provider exposes:

| Method | Return Type | Description |
|--------|-------------|-------------|
| `current()` | `T` | Returns the current value |
| `flow()` | `Flow<T>` | Emits whenever the value changes |

> [!TIP]
> You typically don't interact with providers directly. Use `PlatformSense.environment()` and `PlatformSense.capabilities()` instead. Providers are useful when building custom aggregators or for advanced testing scenarios.

---

## See Also

- **[Getting Started](getting-started.md)** — Installation and first usage
- **[Architecture](architecture.md)** — How providers, repositories, and the facade fit together
- **[Testing Guide](testing-guide.md)** — Fake providers for testing
