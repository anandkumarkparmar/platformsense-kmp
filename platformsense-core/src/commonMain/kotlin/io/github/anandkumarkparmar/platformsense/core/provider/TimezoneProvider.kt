package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.environment.TimezoneInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides current timezone as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks
 * and [flow] for UI that reacts to timezone changes.
 */
interface TimezoneProvider {

    /**
     * Returns the current timezone info at the time of the call.
     */
    fun current(): TimezoneInfo

    /**
     * Emits the current timezone info and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<TimezoneInfo>
}
