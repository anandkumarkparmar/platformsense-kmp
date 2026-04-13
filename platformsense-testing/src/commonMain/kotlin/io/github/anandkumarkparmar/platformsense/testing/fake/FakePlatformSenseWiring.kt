package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.PlatformSenseWiring
import io.github.anandkumarkparmar.platformsense.core.provider.AccessibilityProvider
import io.github.anandkumarkparmar.platformsense.core.provider.AppInfoProvider
import io.github.anandkumarkparmar.platformsense.core.provider.AppearanceProvider
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
import io.github.anandkumarkparmar.platformsense.core.provider.DisplayProvider
import io.github.anandkumarkparmar.platformsense.core.provider.HardwareCapabilitiesProvider
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
import io.github.anandkumarkparmar.platformsense.core.provider.MemoryProvider
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
import io.github.anandkumarkparmar.platformsense.core.provider.StorageProvider
import io.github.anandkumarkparmar.platformsense.core.provider.SystemInfoProvider
import io.github.anandkumarkparmar.platformsense.core.provider.TimezoneProvider

/**
 * [PlatformSenseWiring] built from fake providers for tests.
 *
 * Use with [PlatformSense.initialize][io.github.anandkumarkparmar.platformsense.core.PlatformSense.initialize]
 * to override PlatformSense with configurable fakes. Configure each fake's [currentValue] to simulate
 * platform state (e.g. low power, metered network, dark mode, biometric available).
 *
 * Example:
 * ```
 * val fakes = FakePlatformSenseWiring()
 * fakes.network.currentValue = NetworkInfo(type = NetworkType.CELLULAR, isMetered = true, isConnected = true)
 * fakes.appearance.currentValue = AppearanceInfo(isDarkMode = true)
 * PlatformSense.initialize(fakes)
 * // ... test code using PlatformSense.network.current(), etc.
 * PlatformSense.resetForTest()
 * ```
 */
public class FakePlatformSenseWiring(
    public val network: FakeNetworkProvider = FakeNetworkProvider(),
    public val power: FakePowerProvider = FakePowerProvider(),
    public val locale: FakeLocaleProvider = FakeLocaleProvider(),
    public val timezone: FakeTimezoneProvider = FakeTimezoneProvider(),
    public val appearance: FakeAppearanceProvider = FakeAppearanceProvider(),
    public val display: FakeDisplayProvider = FakeDisplayProvider(),
    public val accessibility: FakeAccessibilityProvider = FakeAccessibilityProvider(),
    public val memory: FakeMemoryProvider = FakeMemoryProvider(),
    public val device: FakeDeviceProvider = FakeDeviceProvider(),
    public val biometric: FakeBiometricProvider = FakeBiometricProvider(),
    public val hardware: FakeHardwareCapabilitiesProvider = FakeHardwareCapabilitiesProvider(),
    public val storage: FakeStorageProvider = FakeStorageProvider(),
    public val systemInfo: FakeSystemInfoProvider = FakeSystemInfoProvider(),
    public val appInfo: FakeAppInfoProvider = FakeAppInfoProvider(),
) : PlatformSenseWiring {

    override fun networkProvider(): NetworkProvider = network

    override fun powerProvider(): PowerProvider = power

    override fun localeProvider(): LocaleProvider = locale

    override fun timezoneProvider(): TimezoneProvider = timezone

    override fun appearanceProvider(): AppearanceProvider = appearance

    override fun displayProvider(): DisplayProvider = display

    override fun accessibilityProvider(): AccessibilityProvider = accessibility

    override fun memoryProvider(): MemoryProvider = memory

    override fun deviceProvider(): DeviceProvider = device

    override fun biometricProvider(): BiometricProvider = biometric

    override fun hardwareCapabilitiesProvider(): HardwareCapabilitiesProvider = hardware

    override fun storageProvider(): StorageProvider = storage

    override fun systemInfoProvider(): SystemInfoProvider = systemInfo

    override fun appInfoProvider(): AppInfoProvider = appInfo
}
