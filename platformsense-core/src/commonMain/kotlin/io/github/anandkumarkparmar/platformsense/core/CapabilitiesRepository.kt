package io.github.anandkumarkparmar.platformsense.core

import io.github.anandkumarkparmar.platformsense.core.models.snapshot.CapabilitiesSnapshot
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider

/**
 * Aggregates capability providers into a single [CapabilitiesSnapshot].
 *
 * Uses [BiometricProvider] for biometric capability. Other capability signals
 * (secure hardware, accessibility) can be added via additional providers in the future;
 * until then they default to false.
 *
 * The biometric provider is resolved lazily on first use.
 */
class CapabilitiesRepository(private val biometricProvider: () -> BiometricProvider) {
    private val biometric: BiometricProvider by lazy { biometricProvider() }

    /**
     * Returns the current capabilities snapshot by querying all providers.
     */
    /**
     * Returns the current capabilities snapshot by querying all providers.
     *
     * Note: [CapabilitiesSnapshot.secureHardwareAvailable] and
     * [CapabilitiesSnapshot.accessibilityAvailable] always return `false` in this version;
     * dedicated providers for these signals are planned for a future release.
     */
    fun current(): CapabilitiesSnapshot = CapabilitiesSnapshot(
        biometric = biometric.current(),
        secureHardwareAvailable = false,
        accessibilityAvailable = false,
    )
}
