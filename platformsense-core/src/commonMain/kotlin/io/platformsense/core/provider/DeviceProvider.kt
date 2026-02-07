package io.platformsense.core.provider

import io.platformsense.domain.DeviceClass
import kotlinx.coroutines.flow.Flow

/**
 * Provides device form factor / class as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks and [flow] for UI that
 * adapts when device class changes (e.g. foldables, tablet mode).
 */
interface DeviceProvider {

    /**
     * Returns the current device class at the time of the call.
     */
    fun current(): DeviceClass

    /**
     * Emits the current device class and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<DeviceClass>
}
