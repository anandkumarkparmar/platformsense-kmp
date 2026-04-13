package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.StorageInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides device storage information as a snapshot and as a reactive stream.
 *
 * Storage values may change over time but are not actively monitored.
 * Use [current] to get a point-in-time snapshot of available storage.
 */
public interface StorageProvider {

    /**
     * Returns the current [StorageInfo] at the time of the call.
     */
    public fun current(): StorageInfo

    /**
     * Emits the current [StorageInfo] and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<StorageInfo>
}
