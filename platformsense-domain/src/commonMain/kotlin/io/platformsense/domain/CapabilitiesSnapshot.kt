package io.platformsense.domain

/**
 * Immutable snapshot of device and runtime capabilities.
 *
 * Aggregates capability signals (biometric, secure hardware, accessibility, etc.)
 * into a single value object for snapshot-style queries. Use this when you need
 * a point-in-time view of what the device supports (e.g. to gate features or adapt UI).
 *
 * New capability properties may be added in future versions with default values
 * to preserve binary and source compatibility.
 *
 * @property biometric Biometric authentication capability (e.g. fingerprint, face).
 * @property secureHardwareAvailable Whether secure hardware (e.g. TEE, Secure Enclave) is available.
 * @property accessibilityAvailable Whether accessibility features are enabled or supported.
 */
data class CapabilitiesSnapshot(
    val biometric: BiometricCapability = BiometricCapability(),
    val secureHardwareAvailable: Boolean = false,
    val accessibilityAvailable: Boolean = false
)
