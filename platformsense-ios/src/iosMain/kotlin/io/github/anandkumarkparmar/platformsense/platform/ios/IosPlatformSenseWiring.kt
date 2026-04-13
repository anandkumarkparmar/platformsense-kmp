package io.github.anandkumarkparmar.platformsense.platform.ios

import io.github.anandkumarkparmar.platformsense.core.PlatformSense
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
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosAccessibilityProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosAppInfoProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosAppearanceProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosBiometricProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosDeviceProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosDisplayProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosHardwareCapabilitiesProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosLocaleProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosMemoryProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosNetworkProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosPowerProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosStorageProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosSystemInfoProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosTimezoneProvider

/**
 * iOS wiring that supplies iOS-specific native providers to
 * [PlatformSense][io.github.anandkumarkparmar.platformsense.core.PlatformSense].
 */
class IosPlatformSenseWiring : PlatformSenseWiring {

    override fun networkProvider(): NetworkProvider = IosNetworkProvider()

    override fun powerProvider(): PowerProvider = IosPowerProvider()

    override fun localeProvider(): LocaleProvider = IosLocaleProvider()

    override fun timezoneProvider(): TimezoneProvider = IosTimezoneProvider()

    override fun appearanceProvider(): AppearanceProvider = IosAppearanceProvider()

    override fun displayProvider(): DisplayProvider = IosDisplayProvider()

    override fun accessibilityProvider(): AccessibilityProvider = IosAccessibilityProvider()

    override fun memoryProvider(): MemoryProvider = IosMemoryProvider()

    override fun deviceProvider(): DeviceProvider = IosDeviceProvider()

    override fun biometricProvider(): BiometricProvider = IosBiometricProvider()

    override fun hardwareCapabilitiesProvider(): HardwareCapabilitiesProvider = IosHardwareCapabilitiesProvider()

    override fun storageProvider(): StorageProvider = IosStorageProvider()

    override fun systemInfoProvider(): SystemInfoProvider = IosSystemInfoProvider()

    override fun appInfoProvider(): AppInfoProvider = IosAppInfoProvider()
}

/**
 * Helper function intended to be called from iOS Swift code (e.g. AppDelegate)
 * to initialize the PlatformSense singleton with iOS providers.
 */
fun initializePlatformSense() {
    PlatformSense.initialize(IosPlatformSenseWiring())
}
