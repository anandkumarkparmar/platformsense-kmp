package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.LocaleInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides current locale as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks
 * and [flow] for UI that reacts to locale changes (e.g. user changes system language).
 */
public interface LocaleProvider {

    /**
     * Returns the current locale info at the time of the call.
     */
    public fun current(): LocaleInfo

    /**
     * Emits the current locale info and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<LocaleInfo>
}
