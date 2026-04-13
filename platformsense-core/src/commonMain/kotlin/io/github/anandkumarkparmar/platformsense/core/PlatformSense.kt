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
 * Unified entry point for platform sensing.
 *
 * Exposes individual providers directly so consumers can pick exactly what they need.
 * Providers are resolved lazily on first access and cached for the lifetime of the facade.
 * Must be initialized by the host (e.g. platform module or app) via [initialize] before use.
 *
 * Usage:
 * ```
 * // One-shot query
 * val networkInfo = PlatformSense.network.current()
 *
 * // Reactive stream
 * PlatformSense.power.flow().collect { powerInfo -> ... }
 * ```
 *
 * @see initialize
 */
object PlatformSense {

    private var wiring: PlatformSenseWiring? = null

    // Cached provider instances
    private var _network: NetworkProvider? = null
    private var _power: PowerProvider? = null
    private var _locale: LocaleProvider? = null
    private var _timezone: TimezoneProvider? = null
    private var _appearance: AppearanceProvider? = null
    private var _display: DisplayProvider? = null
    private var _accessibility: AccessibilityProvider? = null
    private var _memory: MemoryProvider? = null
    private var _device: DeviceProvider? = null
    private var _biometric: BiometricProvider? = null
    private var _hardware: HardwareCapabilitiesProvider? = null
    private var _storage: StorageProvider? = null
    private var _systemInfo: SystemInfoProvider? = null
    private var _appInfo: AppInfoProvider? = null

    // -- State providers (reactive) --

    /** Network connectivity state. */
    val network: NetworkProvider
        get() = _network ?: requireWiring().networkProvider().also { _network = it }

    /** Power and battery state. */
    val power: PowerProvider
        get() = _power ?: requireWiring().powerProvider().also { _power = it }

    /** Locale and regional preferences. */
    val locale: LocaleProvider
        get() = _locale ?: requireWiring().localeProvider().also { _locale = it }

    /** Timezone information. */
    val timezone: TimezoneProvider
        get() = _timezone ?: requireWiring().timezoneProvider().also { _timezone = it }

    /** System appearance (dark mode, dynamic color). */
    val appearance: AppearanceProvider
        get() = _appearance ?: requireWiring().appearanceProvider().also { _appearance = it }

    /** Display and screen information. */
    val display: DisplayProvider
        get() = _display ?: requireWiring().displayProvider().also { _display = it }

    /** Accessibility settings and state. */
    val accessibility: AccessibilityProvider
        get() = _accessibility ?: requireWiring().accessibilityProvider().also { _accessibility = it }

    /** Memory (RAM) information and low-memory warnings. */
    val memory: MemoryProvider
        get() = _memory ?: requireWiring().memoryProvider().also { _memory = it }

    // -- Device providers (static) --

    /** Device identity and form factor. */
    val device: DeviceProvider
        get() = _device ?: requireWiring().deviceProvider().also { _device = it }

    /** Biometric authentication capability. */
    val biometric: BiometricProvider
        get() = _biometric ?: requireWiring().biometricProvider().also { _biometric = it }

    /** Hardware capability flags (camera, NFC, GPS, sensors). */
    val hardware: HardwareCapabilitiesProvider
        get() = _hardware ?: requireWiring().hardwareCapabilitiesProvider().also { _hardware = it }

    /** Device storage information. */
    val storage: StorageProvider
        get() = _storage ?: requireWiring().storageProvider().also { _storage = it }

    /** Extended system and hardware information. */
    val systemInfo: SystemInfoProvider
        get() = _systemInfo ?: requireWiring().systemInfoProvider().also { _systemInfo = it }

    /** App identity and metadata. */
    val appInfo: AppInfoProvider
        get() = _appInfo ?: requireWiring().appInfoProvider().also { _appInfo = it }

    /**
     * Configures the facade from a [PlatformSenseWiring] supplied by a platform module.
     * Call once at app startup (e.g. Android Application.onCreate or iOS AppDelegate).
     *
     * @param wiring Platform wiring supplied by the platform module (e.g. Android or iOS).
     */
    fun initialize(wiring: PlatformSenseWiring) {
        this.wiring = wiring
    }

    /**
     * Returns whether [initialize] has been called.
     */
    fun isInitialized(): Boolean = wiring != null

    /**
     * Clears the configured wiring and cached providers. For test teardown only;
     * allows the next test to call [initialize] with fakes or real wiring.
     * Call in @After or equivalent.
     */
    fun resetForTest() {
        wiring = null
        _network = null
        _power = null
        _locale = null
        _timezone = null
        _appearance = null
        _display = null
        _accessibility = null
        _memory = null
        _device = null
        _biometric = null
        _hardware = null
        _storage = null
        _systemInfo = null
        _appInfo = null
    }

    private fun requireWiring(): PlatformSenseWiring = wiring ?: error(
        "PlatformSense is not initialized. Call PlatformSense.initialize(wiring) before use."
    )
}
