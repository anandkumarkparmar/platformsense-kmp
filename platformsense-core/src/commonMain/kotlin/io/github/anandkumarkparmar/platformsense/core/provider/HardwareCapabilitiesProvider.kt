package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.HardwareCapabilities
import kotlinx.coroutines.flow.Flow

/**
 * Provides hardware capability flags as a snapshot and as a reactive stream.
 *
 * Hardware capabilities are static for the lifetime of the process.
 * Use [current] for feature gating (e.g. "does this device have NFC?").
 */
public interface HardwareCapabilitiesProvider {

    /**
     * Returns the current [HardwareCapabilities] at the time of the call.
     */
    public fun current(): HardwareCapabilities

    /**
     * Emits the current [HardwareCapabilities] and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<HardwareCapabilities>
}
