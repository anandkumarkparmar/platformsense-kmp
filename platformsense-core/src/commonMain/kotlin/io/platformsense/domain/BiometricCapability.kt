package io.platformsense.domain

/**
 * Immutable capability signal for biometric authentication support.
 *
 * Use this to decide whether to show biometric login or other biometric-gated features.
 * The domain model does not specify biometric type (e.g. fingerprint vs face);
 * platform providers map their support into this capability.
 *
 * @property isAvailable True if the device supports biometric authentication and it is
 *        enabled/configured; false otherwise.
 */
data class BiometricCapability(val isAvailable: Boolean = false)
