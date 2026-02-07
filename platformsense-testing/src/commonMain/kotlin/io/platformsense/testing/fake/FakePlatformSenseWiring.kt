package io.platformsense.testing.fake

import io.platformsense.core.CapabilitiesRepository
import io.platformsense.core.EnvironmentRepository
import io.platformsense.core.PlatformSenseWiring

/**
 * [PlatformSenseWiring] built from fake providers for tests.
 *
 * Use with [PlatformSense.initialize][io.platformsense.core.PlatformSense.initialize] to override
 * PlatformSense with configurable fakes. Configure each fake's [currentValue] to simulate
 * environment and capabilities (e.g. low power, metered network, biometric available).
 *
 * Example:
 * ```
 * val fakes = FakePlatformSenseWiring()
 * fakes.network.currentValue = NetworkType.METERED
 * fakes.power.currentValue = PowerState.LOW_POWER
 * PlatformSense.initialize(fakes)
 * // ... test code using PlatformSense.environment(), etc.
 * PlatformSense.resetForTest()
 * ```
 */
class FakePlatformSenseWiring(
    val network: FakeNetworkProvider = FakeNetworkProvider(),
    val power: FakePowerProvider = FakePowerProvider(),
    val device: FakeDeviceProvider = FakeDeviceProvider(),
    val locale: FakeLocaleProvider = FakeLocaleProvider(),
    val timezone: FakeTimezoneProvider = FakeTimezoneProvider(),
    val biometric: FakeBiometricProvider = FakeBiometricProvider(),
) : PlatformSenseWiring {

    override fun environmentRepository(): EnvironmentRepository =
        EnvironmentRepository(
            networkProvider = { network },
            powerProvider = { power },
            deviceProvider = { device },
            localeProvider = { locale },
            timezoneProvider = { timezone },
        )

    override fun capabilitiesRepository(): CapabilitiesRepository =
        CapabilitiesRepository(
            biometricProvider = { biometric },
        )
}
