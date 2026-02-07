package io.platformsense.core

import io.platformsense.domain.CapabilitiesSnapshot
import io.platformsense.domain.EnvironmentSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Unified entry point for environment and capability intelligence.
 *
 * Exposes snapshot and reactive APIs that delegate to [EnvironmentRepository] and
 * [CapabilitiesRepository]. Must be initialized by the host (e.g. platform module or app)
 * via [initialize] before use. Contains no platform logic; all behavior is provided by
 * the configured repositories.
 *
 * @see initialize
 * @see environment
 * @see environmentFlow
 * @see capabilities
 */
object PlatformSense {

    private var environmentRepository: EnvironmentRepository? = null
    private var capabilitiesRepository: CapabilitiesRepository? = null

    /**
     * Configures the facade with the given repositories. Call once at startup from
     * platform code (e.g. Android Application or iOS AppDelegate) after creating
     * repository instances with platform-specific providers.
     *
     * @param environmentRepository Repository that provides environment snapshot and flow.
     * @param capabilitiesRepository Repository that provides capabilities snapshot.
     */
    fun initialize(
        environmentRepository: EnvironmentRepository,
        capabilitiesRepository: CapabilitiesRepository,
    ) {
        this.environmentRepository = environmentRepository
        this.capabilitiesRepository = capabilitiesRepository
    }

    /**
     * Returns whether [initialize] has been called with valid repositories.
     */
    fun isInitialized(): Boolean =
        environmentRepository != null && capabilitiesRepository != null

    private fun requireEnvironmentRepository(): EnvironmentRepository =
        environmentRepository ?: error(
            "PlatformSense is not initialized. Call PlatformSense.initialize() with " +
                "EnvironmentRepository and CapabilitiesRepository before use."
        )

    private fun requireCapabilitiesRepository(): CapabilitiesRepository =
        capabilitiesRepository ?: error(
            "PlatformSense is not initialized. Call PlatformSense.initialize() with " +
                "EnvironmentRepository and CapabilitiesRepository before use."
        )

    /**
     * Returns the current environment snapshot (network, power, device, locale, timezone).
     * Delegates to [EnvironmentRepository.current].
     */
    fun environment(): EnvironmentSnapshot =
        requireEnvironmentRepository().current()

    /**
     * Flow of environment snapshots that emits whenever any environment signal changes.
     * Delegates to [EnvironmentRepository.flow].
     */
    val environmentFlow: Flow<EnvironmentSnapshot>
        get() = requireEnvironmentRepository().flow()

    /**
     * Returns the current capabilities snapshot (biometric, secure hardware, accessibility).
     * Delegates to [CapabilitiesRepository.current].
     */
    fun capabilities(): CapabilitiesSnapshot =
        requireCapabilitiesRepository().current()
}
