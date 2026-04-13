package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.SystemInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides extended system and hardware information as a snapshot and as a reactive stream.
 *
 * System info is static for the lifetime of the process. Use [current] for one-off
 * queries (e.g. diagnostics, emulator detection).
 */
public interface SystemInfoProvider {

    /**
     * Returns the current [SystemInfo] at the time of the call.
     */
    public fun current(): SystemInfo

    /**
     * Emits the current [SystemInfo] and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<SystemInfo>
}
