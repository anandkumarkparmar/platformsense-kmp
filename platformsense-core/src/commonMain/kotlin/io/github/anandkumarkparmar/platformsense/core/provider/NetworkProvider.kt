package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides current network connectivity state as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks and [flow] for UI that
 * reacts to connectivity changes (e.g. metered vs unmetered).
 */
public interface NetworkProvider {

    /**
     * Returns the current network state at the time of the call.
     */
    public fun current(): NetworkInfo

    /**
     * Emits the current network state and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<NetworkInfo>
}
