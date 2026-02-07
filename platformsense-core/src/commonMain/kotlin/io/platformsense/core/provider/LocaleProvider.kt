package io.platformsense.core.provider

import kotlinx.coroutines.flow.Flow

/**
 * Provides current locale as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only (locale identifier string). Use [current] for one-off checks
 * and [flow] for UI that reacts to locale changes (e.g. user changes system language).
 *
 * Locale is typically in BCP 47 form (e.g. "en-US", "ja-JP") or platform-equivalent.
 * Returns an empty string if locale cannot be determined.
 */
interface LocaleProvider {

    /**
     * Returns the current locale identifier at the time of the call.
     * Empty string if unknown.
     */
    fun current(): String

    /**
     * Emits the current locale and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<String>
}
