package io.platformsense.core.provider

import kotlinx.coroutines.flow.Flow

/**
 * Provides current timezone as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only (timezone identifier string). Use [current] for one-off checks
 * and [flow] for UI that reacts to timezone changes.
 *
 * Timezone is typically an IANA identifier (e.g. "America/New_York").
 * Returns an empty string if timezone cannot be determined.
 */
interface TimezoneProvider {

    /**
     * Returns the current timezone identifier at the time of the call.
     * Empty string if unknown.
     */
    fun current(): String

    /**
     * Emits the current timezone and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<String>
}
