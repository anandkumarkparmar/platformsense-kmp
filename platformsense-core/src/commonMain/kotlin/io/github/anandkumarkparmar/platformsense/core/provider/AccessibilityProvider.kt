package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.AccessibilityInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides accessibility settings as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific. Use [current] for one-off checks and [flow]
 * for UI that reacts to accessibility setting changes (e.g. screen reader toggled,
 * reduce motion enabled).
 */
public interface AccessibilityProvider {

    /**
     * Returns the current [AccessibilityInfo] at the time of the call.
     */
    public fun current(): AccessibilityInfo

    /**
     * Emits the current [AccessibilityInfo] and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<AccessibilityInfo>
}
