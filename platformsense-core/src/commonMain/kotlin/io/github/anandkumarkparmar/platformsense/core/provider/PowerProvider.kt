package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerInfo
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
     * Returns the current power info at the time of the call.
     */
    fun current(): PowerInfo

    /**
     * Emits the current power info and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<PowerInfo>
}
