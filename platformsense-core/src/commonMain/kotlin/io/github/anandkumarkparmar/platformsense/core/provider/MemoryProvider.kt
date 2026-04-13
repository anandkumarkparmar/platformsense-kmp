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

    public fun current(): MemoryInfo

    public fun flow(): Flow<MemoryInfo>
}
