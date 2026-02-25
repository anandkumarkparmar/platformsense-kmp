package io.github.anandkumarkparmar.platformsense.platform.ios

import io.github.anandkumarkparmar.platformsense.core.CapabilitiesRepository
import io.github.anandkumarkparmar.platformsense.core.EnvironmentRepository
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.core.PlatformSenseWiring
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosBiometricProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosDeviceProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosLocaleProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosNetworkProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosPowerProvider
import io.github.anandkumarkparmar.platformsense.platform.ios.provider.IosTimezoneProvider

/**
 * iOS wiring that builds [EnvironmentRepository] and [CapabilitiesRepository]
 * using iOS-specific native providers.
 */
class IosPlatformSenseWiring : PlatformSenseWiring {

    override fun environmentRepository(): EnvironmentRepository = EnvironmentRepository(
        networkProvider = { IosNetworkProvider() },
        powerProvider = { IosPowerProvider() },
        deviceProvider = { IosDeviceProvider() },
        localeProvider = { IosLocaleProvider() },
        timezoneProvider = { IosTimezoneProvider() }
    )

    override fun capabilitiesRepository(): CapabilitiesRepository = CapabilitiesRepository(
        biometricProvider = { IosBiometricProvider() }
    )
}

/**
 * Helper function intended to be called from iOS Swift code (e.g. AppDelegate)
 * to initialize the PlatformSense singleton with iOS providers.
 */
fun initializePlatformSense() {
    PlatformSense.initialize(IosPlatformSenseWiring())
}
