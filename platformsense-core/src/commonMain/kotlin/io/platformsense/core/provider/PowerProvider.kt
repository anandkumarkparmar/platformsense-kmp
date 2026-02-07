package io.platformsense.core.provider

import io.platformsense.domain.PowerState
import kotlinx.coroutines.flow.Flow

/**
 * Provides current device power / battery state as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks and [flow] for UI that
 * reacts to power changes (e.g. low power mode, charging).
 */
interface PowerProvider {

    /**
     * Returns the current power state at the time of the call.
     */
    fun current(): PowerState

    /**
     * Emits the current power state and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<PowerState>
}
