package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.DeviceInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides device form factor / class as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks and [flow] for UI that
 * adapts when device class changes (e.g. foldables, tablet mode).
 */
public interface DeviceProvider {

    /**
     * Returns the current device info at the time of the call.
     */
    public fun current(): DeviceInfo

    /**
     * Emits the current device info and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<DeviceInfo>
}
