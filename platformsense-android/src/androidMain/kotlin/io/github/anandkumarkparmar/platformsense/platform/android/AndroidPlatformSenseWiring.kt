package io.github.anandkumarkparmar.platformsense.platform.android

import android.content.Context
import io.github.anandkumarkparmar.platformsense.core.CapabilitiesRepository
import io.github.anandkumarkparmar.platformsense.core.EnvironmentRepository
import io.github.anandkumarkparmar.platformsense.core.PlatformSenseWiring
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidBiometricProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidDeviceProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidLocaleProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidNetworkProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidPowerProvider
import io.github.anandkumarkparmar.platformsense.platform.android.provider.AndroidTimezoneProvider

/**
 * Android implementation of [PlatformSenseWiring].
 *
 * Registers Android providers and supplies [EnvironmentRepository] and [CapabilitiesRepository]
 * built from them. Use from the Application class or main Activity:
 *
 * ```
 * PlatformSense.initialize(AndroidPlatformSenseWiring(context))
 * ```
 *
 * @param context Application or Activity context (held for provider lifetime).
 */
class AndroidPlatformSenseWiring(private val context: Context) : PlatformSenseWiring {

    override fun environmentRepository(): EnvironmentRepository = EnvironmentRepository(
        networkProvider = { AndroidNetworkProvider(context) },
        powerProvider = { AndroidPowerProvider(context) },
        deviceProvider = { AndroidDeviceProvider(context) },
        localeProvider = { AndroidLocaleProvider(context) },
        timezoneProvider = { AndroidTimezoneProvider(context) },
    )

    override fun capabilitiesRepository(): CapabilitiesRepository = CapabilitiesRepository(
        biometricProvider = { AndroidBiometricProvider(context) },
    )
}
