# Changelog

All notable changes to PlatformSense KMP will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [Unreleased]

### Breaking changes

- **Architecture overhaul:** Removed `EnvironmentRepository`, `CapabilitiesRepository`, `EnvironmentSnapshot`, and `CapabilitiesSnapshot`. Providers are now accessed directly on the `PlatformSense` facade.
- **New API:** `PlatformSense.network.current()` / `.flow()` replaces `PlatformSense.environment().networkInfo`. Each provider is accessed independently.
- **Model packages renamed:** `models.environment.*` → `models.state.*` (reactive signals) and `models.device.*` (static device info). `models.capability.*` → `models.device.*`.
- **Wiring interface changed:** `PlatformSenseWiring` now returns individual providers instead of repositories.

### Added

**8 new providers (Android + iOS)**
- `AppearanceProvider` — dark mode detection, dynamic color (Material You) availability
- `DisplayProvider` — screen dimensions (dp + px), density, orientation, refresh rate, notch/cutout detection
- `AccessibilityProvider` — screen reader, bold text, font scale, reduce motion, high contrast, color inversion
- `MemoryProvider` — total RAM, available RAM, low-memory warnings (reactive)
- `HardwareCapabilitiesProvider` — camera, front camera, NFC, GPS, Bluetooth, accelerometer, gyroscope, magnetometer, barometer, vibrator, haptics
- `StorageProvider` — total and available disk space
- `SystemInfoProvider` — API level, SDK version, security patch, build number, emulator detection, CPU architecture, processor count
- `AppInfoProvider` — app name, package/bundle ID, version name, version code, install/update timestamps

**Direct provider access**
- All 14 providers accessible via `PlatformSense.<provider>.current()` and `PlatformSense.<provider>.flow()`
- Providers are lazily initialized and cached on first access

### Removed

- `EnvironmentRepository` and `CapabilitiesRepository`
- `EnvironmentSnapshot` and `CapabilitiesSnapshot`
- `PlatformSense.environment()`, `PlatformSense.environmentFlow`, `PlatformSense.capabilities()`
- `secureHardwareAvailable` and `accessibilityAvailable` stub fields (replaced by real providers)

---

## [v0.1.0] — 2026-02-28

### Added

**Library modules**
- `platformsense-core` — platform-agnostic interfaces, domain models, and the `PlatformSense` façade
- `platformsense-android` — Android implementations of all providers (API 23+)
- `platformsense-ios` — iOS implementations of all providers (iOS 14+)
- `platformsense-testing` — `FakePlatformSenseWiring`, `Fake*Provider` implementations, and `PlatformSenseTestRule` for unit testing

**Environment providers (Android + iOS)**
- `NetworkProvider` — connectivity type (WiFi, Cellular, Ethernet), metered status, roaming status
- `PowerProvider` — battery level, charging state, low-power mode, thermal state
- `DeviceProvider` — device class (Phone, Tablet, TV), OS name/version, manufacturer, model
- `LocaleProvider` — language tag, language code, country code, 24-hour format preference
- `TimezoneProvider` — IANA timezone ID, display name, UTC offset in milliseconds

**Capability providers (Android + iOS)**
- `BiometricProvider` — biometric availability status (Ready, NotEnrolled, Unavailable, NotSupported) and type (Fingerprint, Face, Iris, Multiple)

**Core API**
- `PlatformSense.initialize(wiring)` — single-call platform wiring
- `PlatformSense.environment()` — point-in-time environment snapshot
- `PlatformSense.environmentFlow` — reactive `Flow<EnvironmentSnapshot>` that emits on any signal change
- `PlatformSense.capabilities()` — point-in-time capabilities snapshot
- `PlatformSense.isInitialized()` — initialization guard
- `PlatformSense.resetForTest()` — test teardown helper

**Testing utilities**
- All fake providers expose a `currentValue` setter backed by `MutableStateFlow` for scenario simulation
- `PlatformSenseTestRule` with `install()` / `uninstall()` for clean test lifecycle management

### Platform details

| Platform | Minimum version | Notes |
|----------|----------------|-------|
| Android  | API 23 (Android 6.0) | Network reactive updates require API 24+; biometric detection requires API 30+; thermal state requires API 29+ |
| iOS      | iOS 14+ | Full reactive updates via `NSNotificationCenter` and `nw_path_monitor` |

### Known limitations

- `BiometricProvider` on Android reports `MULTIPLE_OR_GENERIC` for biometric type — distinguishing fingerprint from face requires manufacturer-specific APIs
- `IosNetworkProvider.current()` returns the last-cached value from the flow; the cache starts as the default disconnected state and is populated when the first flow collector starts
- `secureHardwareAvailable` and `accessibilityAvailable` in `CapabilitiesSnapshot` always return `false` — providers for these signals are planned for a future release
- Desktop and Web platforms are not yet supported

[v0.1.0]: https://github.com/anandkumarkparmar/platformsense-kmp/releases/tag/v0.1.0
