package io.platformsense.core

import io.platformsense.core.provider.BiometricProvider
import io.platformsense.domain.CapabilitiesSnapshot

/**
 * Aggregates capability providers into a single [CapabilitiesSnapshot].
 *
 * Uses [BiometricProvider] for biometric capability. Other capability signals
 * (secure hardware, accessibility) can be added via additional providers in the future;
 * until then they default to false.
 *
 * The biometric provider is resolved lazily on first use.
 */
class CapabilitiesRepository(private val biometricProvider: () -> BiometricProvider,) {
    private val biometric: BiometricProvider by lazy { biometricProvider() }

    /**
     * Returns the current capabilities snapshot by querying all providers.
     */
    fun current(): CapabilitiesSnapshot = CapabilitiesSnapshot(
        biometric = biometric.current(),
        secureHardwareAvailable = false,
        accessibilityAvailable = false,
    )
}
