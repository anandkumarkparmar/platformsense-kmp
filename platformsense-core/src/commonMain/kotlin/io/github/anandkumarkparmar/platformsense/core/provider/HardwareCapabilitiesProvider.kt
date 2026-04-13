package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.HardwareCapabilities
import kotlinx.coroutines.flow.Flow

/**
 * Provides hardware capability flags as a snapshot and as a reactive stream.
 *
 * Hardware capabilities are static for the lifetime of the process.
 * Use [current] for feature gating (e.g. "does this device have NFC?").
 */
interface HardwareCapabilitiesProvider {

    fun current(): HardwareCapabilities

    fun flow(): Flow<HardwareCapabilities>
}
