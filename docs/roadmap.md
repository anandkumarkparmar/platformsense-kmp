# Roadmap

PlatformSense KMP development roadmap — current status, near-term plans, and long-term vision.

---

## Current Status (v0.1.0)

### Environment Providers

| Provider | Status |
|----------|--------|
| `NetworkProvider` | ✅ Implemented (Android + iOS) |
| `PowerProvider` | ✅ Implemented (Android + iOS) |
| `DeviceProvider` | ✅ Implemented (Android + iOS) |
| `LocaleProvider` | ✅ Implemented (Android + iOS) |
| `TimezoneProvider` | ✅ Implemented (Android + iOS) |

### Capability Providers

| Provider | Status |
|----------|--------|
| `BiometricProvider` | ✅ Implemented (Android + iOS) |
| `SecureHardwareProvider` | 🔜 Planned |
| `AccessibilityProvider` | 🔜 Planned |

### Platform Support

| Platform | Status | Notes |
|----------|--------|-------|
| **Android** | ✅ Supported | API 23+ (Android 6.0+) |
| **iOS** | ✅ Supported | iOS 14+ |
| **Desktop (JVM)** | 🔜 Planned | — |
| **Web (JS/WASM)** | 🔜 Planned | — |

---

## Near-Term Goals

### Reactive Capabilities Flow

Add a `capabilitiesFlow` to complement the existing `environmentFlow`:

```kotlin
// Planned API:
PlatformSense.capabilitiesFlow.collect { caps ->
    updateBiometricUI(caps.biometric)
}
```

### Desktop Platform Providers

Implement JVM-based providers for Desktop targets:

| Provider | Planned Approach |
|----------|-----------------|
| `NetworkProvider` | `java.net.NetworkInterface` |
| `DeviceProvider` | `System.getProperty()`, always `DeviceClass.DESKTOP` |
| `LocaleProvider` | `java.util.Locale` |
| `TimezoneProvider` | `java.util.TimeZone` |
| `PowerProvider` | OS-specific via JNA or `UNKNOWN` fallback |
| `BiometricProvider` | `NOT_SUPPORTED` initially |

### Secure Hardware Provider

Detect whether the device has a secure element (TEE, Secure Enclave):

```kotlin
// Planned API:
val isSecure = PlatformSense.capabilities().secureHardwareAvailable
```

---

## Mid-Term Goals

### New Environment Providers

| Provider | Description | Priority |
|----------|-------------|----------|
| `ThermalProvider` | Dedicated thermal state provider (currently part of `PowerInfo`) | Medium |
| `AccessibilityStateProvider` | Detect enabled accessibility services (TalkBack, VoiceOver, font scale) | Medium |
| `NetworkQualityProvider` | Connection speed estimation, latency hints | Medium |
| `PerformanceHintProvider` | CPU/GPU hints for adaptive performance | Low |
| `BatteryHealthProvider` | Battery health and degradation level | Low |
| `DisplayEnvironmentProvider` | Screen density, refresh rate, HDR support | Low |

### New Capability Providers

| Provider | Description | Priority |
|----------|-------------|----------|
| `CameraCapabilityProvider` | Front/back camera availability, resolution | Medium |
| `ConnectivityCapabilityProvider` | NFC, Bluetooth, UWB availability | Medium |
| `InputCapabilityProvider` | Stylus, keyboard, mouse availability | Low |
| `SecurityAttestationCapabilityProvider` | SafetyNet / App Attest availability | Low |

### Web Platform Providers

| Provider | Planned Approach |
|----------|-----------------|
| `NetworkProvider` | `navigator.onLine`, `navigator.connection` |
| `PowerProvider` | Battery Status API |
| `DeviceProvider` | User agent parsing, media queries |
| `LocaleProvider` | `navigator.language` |
| `TimezoneProvider` | `Intl.DateTimeFormat` |
| `BiometricProvider` | WebAuthn / FIDO2 |

---

## Long-Term Vision

### Future Modules

As the library matures, specialized concerns may be extracted into dedicated modules:

```
platformsense-core                  # Core (exists)
platformsense-android               # Android providers (exists)
platformsense-testing               # Test utilities (exists)
platformsense-security              # Security attestation, key management
platformsense-performance           # Performance hints, thermal management
platformsense-accessibility         # Accessibility state, font scale, screen reader
platformsense-ui-adaptation         # Layout helpers, responsive design utilities
```

### Compose Multiplatform Extensions

Planned Compose-specific utilities:

```kotlin
// Planned helper composable:
@Composable
fun rememberEnvironment(): State<EnvironmentSnapshot> {
    return PlatformSense.environmentFlow.collectAsState(
        initial = PlatformSense.environment()
    )
}

// Planned environment-aware modifier:
Modifier.environmentAware { env ->
    if (env.powerInfo.status == PowerState.LOW_POWER) {
        disableAnimations()
    }
}
```

### Ecosystem Goal

PlatformSense aims to become the **standard environment + capability SDK** for Kotlin Multiplatform, similar to:

| Library | Domain |
|---------|--------|
| **Ktor** | Networking |
| **Koin** | Dependency injection |
| **Coil** | Image loading |
| **PlatformSense** | Environment + capability intelligence |

---

## How to Contribute

Interested in working on any of these items? See the [Contributing Guide](contributing.md) for how to get started. Feature proposals are welcome — open an issue describing your use case and proposed API.

---

## See Also

- **[Platform Support](platform-support.md)** — Current platform implementation details
- **[Architecture](architecture.md)** — How new providers fit into the existing design
- **[Contributing](contributing.md)** — How to contribute to the roadmap
