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

    public fun current(): StorageInfo

    public fun flow(): Flow<StorageInfo>
}
