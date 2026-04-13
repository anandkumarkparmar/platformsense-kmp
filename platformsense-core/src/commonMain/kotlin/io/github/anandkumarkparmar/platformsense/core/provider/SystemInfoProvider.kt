package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.SystemInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides extended system and hardware information as a snapshot and as a reactive stream.
 *
 * System info is static for the lifetime of the process. Use [current] for one-off
 * queries (e.g. diagnostics, emulator detection).
 */
interface SystemInfoProvider {

    fun current(): SystemInfo

    fun flow(): Flow<SystemInfo>
}
