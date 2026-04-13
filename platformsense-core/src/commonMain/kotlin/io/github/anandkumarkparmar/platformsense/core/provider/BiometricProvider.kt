package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides biometric authentication capability as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks (e.g. show biometric login)
 * and [flow] for UI that reacts when capability changes (e.g. user enrolls a fingerprint).
 */
public interface BiometricProvider {

    /**
     * Returns the current biometric capability at the time of the call.
     */
    public fun current(): BiometricInfo

    /**
     * Emits the current biometric capability and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    public fun flow(): Flow<BiometricInfo>
}
