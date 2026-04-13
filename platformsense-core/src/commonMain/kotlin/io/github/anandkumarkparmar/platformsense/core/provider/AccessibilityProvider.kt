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

    public fun current(): AccessibilityInfo

    public fun flow(): Flow<AccessibilityInfo>
}
