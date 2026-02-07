package io.platformsense.core

/**
 * Supplies [EnvironmentRepository] and [CapabilitiesRepository] built with platform-specific providers.
 *
 * Platform modules implement this interface and pass an instance to [PlatformSense.initialize].
 * Core does not depend on any platform; it only consumes this abstraction.
 *
 * Example (Android): create a class that holds [Context], instantiates Android providers,
 * and returns repositories built from them. Then call
 * `PlatformSense.initialize(AndroidPlatformSenseWiring(context))`.
 */
interface PlatformSenseWiring {

    /**
     * Returns the environment repository (network, power, device, locale, timezone).
     * Called once during [PlatformSense.initialize].
     */
    fun environmentRepository(): EnvironmentRepository

    /**
     * Returns the capabilities repository (biometric, etc.).
     * Called once during [PlatformSense.initialize].
     */
    fun capabilitiesRepository(): CapabilitiesRepository
}
