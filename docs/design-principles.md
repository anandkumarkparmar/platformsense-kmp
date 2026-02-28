# Design Principles

The core design philosophy behind PlatformSense KMP — the "why" behind every architectural decision.

---

## 1. Domain-First Design

> **Public APIs must use domain models, never platform APIs.**

PlatformSense never exposes `ConnectivityManager`, `NWPathMonitor`, `UIDevice`, or any platform type in its public API. Instead, everything flows through domain models like `NetworkState`, `PowerInfo`, and `BiometricCapability`.

### Why?

| Benefit | Explanation |
|---------|-------------|
| **Portability** | Domain models work across all platforms. Your business logic doesn't change when you add iOS or Desktop support. |
| **Testability** | You can construct domain models directly in tests — no mocking of Android `Context` or iOS `NSObject`. |
| **Stability** | Domain models are under our control. Platform APIs change between OS versions; our models don't. |
| **Discoverability** | A clean, small API surface is easier to learn than a wrapper around platform specifics. |

### In Practice

```kotlin
// ✅ Domain-first — this works everywhere, testable without a device
val network: NetworkState = PlatformSense.environment().networkState
if (network.isMetered) { /* adapt */ }

// ❌ What we avoid — tied to Android, untestable without mocking
val cm = context.getSystemService(ConnectivityManager::class.java)
val caps = cm.getNetworkCapabilities(cm.activeNetwork)
val isMetered = !caps.hasCapability(NET_CAPABILITY_NOT_METERED)
```

---

## 2. Reactive + Snapshot

> **Support both one-shot queries and `Flow`-based reactive streams.**

PlatformSense provides two ways to access environment data:

| Access Pattern | Method | Best For |
|---------------|--------|----------|
| **Snapshot** | `PlatformSense.environment()` | One-time decisions, initialization logic, conditional checks |
| **Reactive** | `PlatformSense.environmentFlow` | UI that reacts to changes, continuous monitoring, Compose `collectAsState` |

### Why Both?

- **Snapshot** is simpler — no coroutine scope needed, no collection, no lifecycle management. Perfect for a quick `if` check.
- **Reactive** is essential for UIs that must update when the user goes offline, plugs in a charger, or changes locale.

### Performance

- **Snapshot** (`current()`) queries all providers synchronously and is lightweight
- **Reactive** (`flow()`) uses `combine` to merge provider flows — work only happens when something collects

---

## 3. Platform-Safe

> **If a platform does not support a signal, return UNKNOWN — never crash.**

Every enum in PlatformSense includes an `UNKNOWN` value. Every numeric property has a `null` or `0` default. Every string property defaults to `""`.

| Signal | Unsupported Behavior |
|--------|---------------------|
| `NetworkType` | Returns `UNKNOWN` |
| `PowerState` | Returns `UNKNOWN` |
| `ThermalState` | Returns `UNKNOWN` (e.g. Android currently) |
| `DeviceClass` | Returns `UNKNOWN` |
| `BiometricStatus` | Returns `NOT_SUPPORTED` |
| `batteryLevel` | Returns `null` |
| String properties | Returns `""` |

### Why?

- **No crashes on new platforms** — if you add Desktop support tomorrow, everything works with safe defaults
- **Graceful degradation** — features that depend on unsupported signals simply skip instead of throwing
- **Defensive coding** — encourages `when` blocks with `else` branches

```kotlin
// ✅ Safe — always works, even on platforms without thermal support
when (env.powerInfo.thermalState) {
    ThermalState.SERIOUS, ThermalState.CRITICAL -> throttle()
    else -> proceedNormally() // includes UNKNOWN
}
```

---

## 4. Lightweight

> **PlatformSense must feel like a standard library extension.**

### Design Goals

| Aspect | Goal |
|--------|------|
| **Binary size** | Minimal — only data classes, enums, and thin provider wrappers |
| **Dependencies** | Only Kotlin stdlib + Coroutines (core); platform SDKs in platform modules |
| **Initialization** | Single line: `PlatformSense.initialize(wiring)` |
| **API surface** | Three main methods: `environment()`, `environmentFlow`, `capabilities()` |
| **Memory** | Lazy provider initialization — nothing loads until you ask for it |
| **Performance** | No polling, no background threads — reactive via platform callbacks |

### What We Avoid

- ❌ Heavy DI frameworks as a hard dependency
- ❌ Annotation processing or code generation
- ❌ Background services or scheduled Jobs
- ❌ Network calls or analytics
- ❌ Persistent storage or databases

---

## 5. Test-First

> **Testing injection is a first-class citizen, not an afterthought.**

PlatformSense was designed from day one with testability in mind:

| Feature | Description |
|---------|-------------|
| **Dedicated testing module** | `platformsense-testing` ships separately — no test code in production |
| **Fake providers** | One fake per provider, all with `currentValue` you can set |
| **Test rule** | `PlatformSenseTestRule` handles lifecycle automatically |
| **No mocking needed** | Domain models are data classes — construct them directly |
| **Resetable state** | `PlatformSense.resetForTest()` ensures test isolation |

### Why First-Class Testing?

Environment-dependent code is notoriously hard to test. How do you simulate "low battery on a metered network" in a unit test? PlatformSense makes this trivial:

```kotlin
// In one line, you're in a simulated environment:
rule.wiring.power.currentValue = PowerInfo(status = PowerState.LOW_POWER)
rule.wiring.network.currentValue = NetworkState(isMetered = true, isConnected = true)

// Now test your feature logic against these exact conditions
```

---

## 6. Coding Guidelines

These guidelines govern contributions to PlatformSense itself:

### Immutable Data Models

All domain models are Kotlin `data class` with `val` properties. No mutable state leaks into the public API.

```kotlin
// ✅ Immutable data class
data class NetworkState(
    val type: NetworkType = NetworkType.UNKNOWN,
    val isConnected: Boolean = false,
    val isMetered: Boolean = false,
    val isRoaming: Boolean = false
)
```

### Flow for Reactive Signals

Every provider exposes a `flow()` function returning `Flow<T>`. No callbacks, no custom observer patterns.

### Thin Provider Implementations

Platform providers should be thin wrappers around native APIs. Complex logic belongs in the repository or domain layer, not in providers.

### Composition Over Inheritance

- Providers are composed into repositories
- Repositories are composed into the facade
- No deep inheritance hierarchies

### Avoid Platform Logic in Domain Module

The `io.github.anandkumarkparmar.platformsense.domain` package must never import platform-specific types. Not even `android.os.Build` or `platform.Foundation.NSLocale`.

---

## See Also

- **[Architecture](architecture.md)** — How these principles manifest in the code structure
- **[Contributing](contributing.md)** — Guidelines for contributing code
- **[API Reference](api-reference.md)** — The public API shaped by these principles
