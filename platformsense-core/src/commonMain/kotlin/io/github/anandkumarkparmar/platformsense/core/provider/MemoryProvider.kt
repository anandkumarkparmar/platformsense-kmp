package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.MemoryInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides device memory (RAM) information as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific. Use [current] for one-off checks and [flow]
 * for reactive low-memory warnings.
 */
public interface MemoryProvider {

    /**
     * Returns the current [MemoryInfo] at the time of the call.
     */
    public fun current(): MemoryInfo

    /**
     * Emits the current [MemoryInfo] and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<MemoryInfo>
}
