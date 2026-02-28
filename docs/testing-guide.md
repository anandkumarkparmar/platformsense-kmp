# Testing Guide

How to test PlatformSense-dependent code using the `platformsense-testing` module — fake providers, test rules, and testing patterns.

---

## Overview

The `platformsense-testing` module provides **fake implementations** of every provider and a **test rule** that lets you fully control environment and capability signals in unit tests. No real device state, no flaky tests.

---

## Setup

Add the testing module to your test dependencies:

```kotlin
commonTest.dependencies {
    implementation("com.github.anandkumarkparmar.platformsense-kmp:platformsense-testing:0.1.0")
}
```

---

## PlatformSenseTestRule

The easiest way to use PlatformSense in tests. Handles initialization and teardown automatically.

### Lifecycle

```kotlin
import io.github.anandkumarkparmar.platformsense.testing.PlatformSenseTestRule
import io.github.anandkumarkparmar.platformsense.domain.PowerState

class MyFeatureTest {

    private val rule = PlatformSenseTestRule()

    @Before
    fun setup() {
        rule.install()  // Initializes PlatformSense with fake providers
    }

    @After
    fun teardown() {
        rule.uninstall()  // Clears PlatformSense for the next test
    }

    @Test
    fun `reduces animations in low power mode`() {
        // Arrange
        rule.wiring.power.currentValue = PowerInfo(status = PowerState.LOW_POWER)

        // Act
        val env = PlatformSense.environment()

        // Assert
        assertEquals(PowerState.LOW_POWER, env.powerInfo.status)
    }
}
```

### How It Works

1. **`install()`** — Creates a `FakePlatformSenseWiring` with all fake providers and calls `PlatformSense.initialize(wiring)`
2. **Configure fakes** — Set `currentValue` on any fake provider before calling `environment()` or `capabilities()`
3. **`uninstall()`** — Calls `PlatformSense.resetForTest()` to clear state, ensuring test isolation

> [!IMPORTANT]
> Always call `uninstall()` in `@After`. If you forget, the next test will fail with "PlatformSense is already initialized."

---

## Fake Providers

Each real provider has a corresponding fake in `io.github.anandkumarkparmar.platformsense.testing.fake`. All fakes follow the same pattern:

```kotlin
class FakeNetworkProvider : NetworkProvider {
    var currentValue: NetworkState = NetworkState()  // Default state
    override fun current(): NetworkState = currentValue
    override fun flow(): Flow<NetworkState> = MutableStateFlow(currentValue)
}
```

### Available Fakes

| Fake Class | Controls | Default Value |
|-----------|----------|---------------|
| `FakeNetworkProvider` | `NetworkState` | `NetworkState()` — Unknown, disconnected |
| `FakePowerProvider` | `PowerInfo` | `PowerInfo()` — Unknown status, null battery |
| `FakeDeviceProvider` | `DeviceInfo` | `DeviceInfo()` — Unknown device class |
| `FakeLocaleProvider` | `LocaleInfo` | `LocaleInfo()` — Empty strings |
| `FakeTimezoneProvider` | `TimezoneInfo` | `TimezoneInfo()` — Empty strings, 0 offset |
| `FakeBiometricProvider` | `BiometricCapability` | `BiometricCapability()` — Not supported |

### Accessing Fakes

All fakes are accessible via the `FakePlatformSenseWiring` on the test rule:

```kotlin
val rule = PlatformSenseTestRule()

// Access individual fake providers:
rule.wiring.network    // FakeNetworkProvider
rule.wiring.power      // FakePowerProvider
rule.wiring.device     // FakeDeviceProvider
rule.wiring.locale     // FakeLocaleProvider
rule.wiring.timezone   // FakeTimezoneProvider
rule.wiring.biometric  // FakeBiometricProvider
```

---

## FakePlatformSenseWiring

Wires all fake providers into the repositories that PlatformSense uses. You rarely interact with this directly — use `PlatformSenseTestRule` instead.

```kotlin
class FakePlatformSenseWiring : PlatformSenseWiring {
    val network = FakeNetworkProvider()
    val power = FakePowerProvider()
    val device = FakeDeviceProvider()
    val locale = FakeLocaleProvider()
    val timezone = FakeTimezoneProvider()
    val biometric = FakeBiometricProvider()

    override fun environmentRepository() = EnvironmentRepository(
        networkProvider  = { network },
        powerProvider    = { power },
        deviceProvider   = { device },
        localeProvider   = { locale },
        timezoneProvider = { timezone },
    )

    override fun capabilitiesRepository() = CapabilitiesRepository(
        biometricProvider = { biometric },
    )
}
```

---

## Testing Patterns

### Pattern 1: Test Feature Gating

Test that features are correctly enabled/disabled based on environment signals.

```kotlin
@Test
fun `shows biometric login when biometrics are ready`() {
    rule.wiring.biometric.currentValue = BiometricCapability(
        status = BiometricStatus.READY,
        type = BiometricType.FINGERPRINT
    )

    val caps = PlatformSense.capabilities()
    assertTrue(caps.biometric.status == BiometricStatus.READY)
    // Assert your feature gating logic shows biometric option
}

@Test
fun `hides biometric login when not supported`() {
    rule.wiring.biometric.currentValue = BiometricCapability(
        status = BiometricStatus.NOT_SUPPORTED
    )

    val caps = PlatformSense.capabilities()
    assertTrue(caps.biometric.status == BiometricStatus.NOT_SUPPORTED)
    // Assert your feature gating logic hides biometric option
}
```

### Pattern 2: Test Multiple Signals Together

Test behavior that depends on a combination of signals.

```kotlin
@Test
fun `uses low quality images on metered network in low power`() {
    rule.wiring.network.currentValue = NetworkState(
        type = NetworkType.CELLULAR,
        isConnected = true,
        isMetered = true
    )
    rule.wiring.power.currentValue = PowerInfo(
        status = PowerState.LOW_POWER
    )

    val env = PlatformSense.environment()
    assertTrue(env.networkState.isMetered)
    assertEquals(PowerState.LOW_POWER, env.powerInfo.status)
    // Assert low quality image loading logic
}
```

### Pattern 3: Test Edge Cases (UNKNOWN values)

Verify graceful handling of unknown/unsupported signals.

```kotlin
@Test
fun `handles unknown network type gracefully`() {
    rule.wiring.network.currentValue = NetworkState(
        type = NetworkType.UNKNOWN,
        isConnected = false
    )

    val env = PlatformSense.environment()
    assertEquals(NetworkType.UNKNOWN, env.networkState.type)
    // Assert your code falls back to a safe default
}
```

### Pattern 4: Test Device-Specific Layouts

```kotlin
@Test
fun `uses multi-pane layout on tablet`() {
    rule.wiring.device.currentValue = DeviceInfo(
        deviceClass = DeviceClass.TABLET,
        osName = "Android",
        osVersion = "14"
    )

    val env = PlatformSense.environment()
    assertEquals(DeviceClass.TABLET, env.deviceInfo.deviceClass)
    // Assert multi-pane layout selection
}
```

### Pattern 5: Test Locale-Dependent Behavior

```kotlin
@Test
fun `formats time in 24-hour format for German locale`() {
    rule.wiring.locale.currentValue = LocaleInfo(
        languageTag = "de-DE",
        language = "de",
        country = "DE",
        is24HourFormat = true
    )

    val env = PlatformSense.environment()
    assertTrue(env.localeInfo.is24HourFormat)
    assertEquals("de-DE", env.localeInfo.languageTag)
}
```

---

## Integration with Test Frameworks

### JUnit 4

The examples above use JUnit 4 (`@Before` / `@After`). This is the simplest approach.

### JUnit 5

```kotlin
@ExtendWith(/* your extensions */)
class MyFeatureTest {

    private val rule = PlatformSenseTestRule()

    @BeforeEach
    fun setup() { rule.install() }

    @AfterEach
    fun teardown() { rule.uninstall() }

    @Test
    fun myTest() { /* ... */ }
}
```

### Kotest

```kotlin
class MyFeatureSpec : FunSpec({
    val rule = PlatformSenseTestRule()

    beforeTest { rule.install() }
    afterTest { rule.uninstall() }

    test("low power mode reduces animations") {
        rule.wiring.power.currentValue = PowerInfo(status = PowerState.LOW_POWER)
        // ...
    }
})
```

---

## Tips

> [!TIP]
> - Always set the fake values **before** calling `PlatformSense.environment()` or `PlatformSense.capabilities()`
> - Test both "happy path" and edge cases (UNKNOWN, NOT_SUPPORTED, null battery level)
> - Combine multiple fakes to test complex feature gating scenarios
> - Use descriptive test names that mention the environment condition being simulated

---

## See Also

- **[API Reference](api-reference.md)** — Domain model properties and enums
- **[Use Cases & Recipes](use-cases-and-recipes.md)** — Patterns to test against
- **[Getting Started](getting-started.md)** — Adding the testing dependency
