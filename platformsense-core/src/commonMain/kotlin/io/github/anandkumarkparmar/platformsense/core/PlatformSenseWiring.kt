package io.github.anandkumarkparmar.platformsense.core

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
 * Supplies platform-specific provider implementations to [PlatformSense].
 *
 * Platform modules implement this interface and pass an instance to [PlatformSense.initialize].
 * Core does not depend on any platform; it only consumes this abstraction.
 *
 * Each method returns a provider that is resolved lazily by the facade on first access.
 */
interface PlatformSenseWiring {

    // -- State providers (reactive — values change at runtime) --

    fun networkProvider(): NetworkProvider

    fun powerProvider(): PowerProvider

    fun localeProvider(): LocaleProvider

    fun timezoneProvider(): TimezoneProvider

    fun appearanceProvider(): AppearanceProvider

    fun displayProvider(): DisplayProvider

    fun accessibilityProvider(): AccessibilityProvider

    fun memoryProvider(): MemoryProvider

    // -- Device providers (static — values rarely change) --

    fun deviceProvider(): DeviceProvider

    fun biometricProvider(): BiometricProvider

    fun hardwareCapabilitiesProvider(): HardwareCapabilitiesProvider

    fun storageProvider(): StorageProvider

    fun systemInfoProvider(): SystemInfoProvider

    fun appInfoProvider(): AppInfoProvider
}
