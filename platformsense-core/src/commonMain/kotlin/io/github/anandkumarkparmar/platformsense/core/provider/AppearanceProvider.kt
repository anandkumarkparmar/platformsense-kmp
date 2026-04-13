package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.AppearanceInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides the current system appearance (dark mode, dynamic color) as a snapshot
 * and as a reactive stream.
 *
 * Implementations are platform-specific. Use [current] for one-off checks and [flow]
 * for UI that reacts to dark mode changes.
 */
public interface AppearanceProvider {

    /**
     * Returns the current appearance info at the time of the call.
     */
    public fun current(): AppearanceInfo

    /**
     * Emits the current appearance info and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<AppearanceInfo>
}
