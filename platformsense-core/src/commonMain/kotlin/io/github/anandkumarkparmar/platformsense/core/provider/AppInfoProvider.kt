package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.AppInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides app identity and metadata as a snapshot and as a reactive stream.
 *
 * App info is static for the lifetime of the process. Use [current] for one-off
 * queries (e.g. about screen, analytics events).
 */
public interface AppInfoProvider {

    /**
     * Returns the current [AppInfo] at the time of the call.
     */
    public fun current(): AppInfo

    /**
     * Emits the current [AppInfo] and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<AppInfo>
}
