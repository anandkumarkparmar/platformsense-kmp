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
public interface PlatformSenseWiring {

    // -- State providers (reactive — values change at runtime) --

    /** Creates the [NetworkProvider] for this platform. */
    public fun networkProvider(): NetworkProvider

    /** Creates the [PowerProvider] for this platform. */
    public fun powerProvider(): PowerProvider

    /** Creates the [LocaleProvider] for this platform. */
    public fun localeProvider(): LocaleProvider

    /** Creates the [TimezoneProvider] for this platform. */
    public fun timezoneProvider(): TimezoneProvider

    /** Creates the [AppearanceProvider] for this platform. */
    public fun appearanceProvider(): AppearanceProvider

    /** Creates the [DisplayProvider] for this platform. */
    public fun displayProvider(): DisplayProvider

    /** Creates the [AccessibilityProvider] for this platform. */
    public fun accessibilityProvider(): AccessibilityProvider

    /** Creates the [MemoryProvider] for this platform. */
    public fun memoryProvider(): MemoryProvider

    // -- Device providers (static — values rarely change) --

    /** Creates the [DeviceProvider] for this platform. */
    public fun deviceProvider(): DeviceProvider

    /** Creates the [BiometricProvider] for this platform. */
    public fun biometricProvider(): BiometricProvider

    /** Creates the [HardwareCapabilitiesProvider] for this platform. */
    public fun hardwareCapabilitiesProvider(): HardwareCapabilitiesProvider

    /** Creates the [StorageProvider] for this platform. */
    public fun storageProvider(): StorageProvider

    /** Creates the [SystemInfoProvider] for this platform. */
    public fun systemInfoProvider(): SystemInfoProvider

    /** Creates the [AppInfoProvider] for this platform. */
    public fun appInfoProvider(): AppInfoProvider
}
