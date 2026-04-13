package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.DisplayInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides display and screen information as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific. Use [current] for one-off checks and [flow]
 * for UI that reacts to orientation or display configuration changes.
 */
interface DisplayProvider {

    /**
     * Returns the current display info at the time of the call.
     */
    fun current(): DisplayInfo

    /**
     * Emits the current display info and then whenever it changes
     * (e.g. orientation change, display connection).
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<DisplayInfo>
}
