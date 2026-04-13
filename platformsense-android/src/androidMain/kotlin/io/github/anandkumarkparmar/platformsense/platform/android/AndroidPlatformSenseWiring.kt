package io.github.anandkumarkparmar.platformsense.platform.android

import android.content.Context
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
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidAccessibilityProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidAppInfoProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidAppearanceProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidBiometricProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidDeviceProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidDisplayProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidHardwareCapabilitiesProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidLocaleProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidMemoryProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidNetworkProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidPowerProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidStorageProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidSystemInfoProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidTimezoneProvider

/**
 * Android implementation of [PlatformSenseWiring].
 *
 * Supplies Android-specific provider implementations to [PlatformSense][io.github.anandkumarkparmar.platformsense.core.PlatformSense].
 * Use from the Application class or main Activity:
 *
 * ```
 * PlatformSense.initialize(AndroidPlatformSenseWiring(context))
 * ```
 *
 * @param context Application or Activity context (held for provider lifetime).
 */
public class AndroidPlatformSenseWiring(private val context: Context) : PlatformSenseWiring {

    override fun networkProvider(): NetworkProvider = AndroidNetworkProvider(context)

    override fun powerProvider(): PowerProvider = AndroidPowerProvider(context)

    override fun localeProvider(): LocaleProvider = AndroidLocaleProvider(context)

    override fun timezoneProvider(): TimezoneProvider = AndroidTimezoneProvider(context)

    override fun appearanceProvider(): AppearanceProvider = AndroidAppearanceProvider(context)

    override fun displayProvider(): DisplayProvider = AndroidDisplayProvider(context)

    override fun accessibilityProvider(): AccessibilityProvider = AndroidAccessibilityProvider(context)

    override fun memoryProvider(): MemoryProvider = AndroidMemoryProvider(context)

    override fun deviceProvider(): DeviceProvider = AndroidDeviceProvider(context)

    override fun biometricProvider(): BiometricProvider = AndroidBiometricProvider(context)

    override fun hardwareCapabilitiesProvider(): HardwareCapabilitiesProvider =
        AndroidHardwareCapabilitiesProvider(context)

    override fun storageProvider(): StorageProvider = AndroidStorageProvider()

    override fun systemInfoProvider(): SystemInfoProvider = AndroidSystemInfoProvider()

    override fun appInfoProvider(): AppInfoProvider = AndroidAppInfoProvider(context)
}
