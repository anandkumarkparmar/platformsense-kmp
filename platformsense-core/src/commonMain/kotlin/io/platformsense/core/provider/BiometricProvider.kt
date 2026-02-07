package io.platformsense.core.provider

import io.platformsense.domain.BiometricCapability
import kotlinx.coroutines.flow.Flow

/**
 * Provides biometric authentication capability as a snapshot and as a reactive stream.
 *
 * Implementations are platform-specific; this interface lives in core and returns
 * domain models only. Use [current] for one-off checks (e.g. show biometric login)
 * and [flow] for UI that reacts when capability changes (e.g. user enrolls a fingerprint).
 */
interface BiometricProvider {

    /**
     * Returns the current biometric capability at the time of the call.
     */
    fun current(): BiometricCapability

    /**
     * Emits the current biometric capability and then whenever it changes.
     * Implementations should emit at least once and then on each change.
     */
    fun flow(): Flow<BiometricCapability>
}
