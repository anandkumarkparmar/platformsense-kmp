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

    public fun current(): HardwareCapabilities

    public fun flow(): Flow<HardwareCapabilities>
}
