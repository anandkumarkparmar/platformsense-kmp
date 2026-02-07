package io.platformsense.core.provider

import io.platformsense.domain.NetworkType
import kotlinx.coroutines.flow.Flow

/**
 * Provides current network connectivity type as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks and [flow] for UI that
 * reacts to connectivity changes (e.g. metered vs unmetered).
 */
interface NetworkProvider {

    /**
     * Returns the current network type at the time of the call.
     */
    fun current(): NetworkType

    /**
     * Emits the current network type and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<NetworkType>
}
