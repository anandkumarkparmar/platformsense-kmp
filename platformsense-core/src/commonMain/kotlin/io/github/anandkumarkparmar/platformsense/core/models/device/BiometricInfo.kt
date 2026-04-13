package io.github.anandkumarkparmar.platformsense.core.models.device

/**
 * Immutable capability signal for biometric authentication support.
 *
 * Use this to decide whether to show biometric login or other biometric-gated features.
 * Platform providers map their support into this capability.
 *
 * @property status The current enrollment and availability status of biometrics.
 * @property type The type of expected primary biometric (e.g. FINGERPRINT, FACE), if known.
 */
data class BiometricInfo(
    val status: BiometricStatus = BiometricStatus.NOT_SUPPORTED,
    val type: BiometricType = BiometricType.UNKNOWN
)

/**
 * Represents the current status of biometric authentication on the device.
 */
enum class BiometricStatus {
    /** Biometric hardware is present and user has enrolled biometric templates. Ready to use. */
    READY,

    /** Biometric hardware is present, but the user has not enrolled any templates. */
    NOT_ENROLLED,

    /** Biometric hardware is unavailable, locked out, or temporarily disabled. */
    UNAVAILABLE,

    /** The device does not support biometric authentication at all. */
    NOT_SUPPORTED
}

/**
 * Represents the primary type of biometric hardware present on the device.
 */
enum class BiometricType {
    /** Fingerprint or Touch ID sensor. */
    FINGERPRINT,

    /** Face recognition or Face ID. */
    FACE,

    /** Iris scanner. */
    IRIS,

    /** Multiple types available, or specific type cannot be determined but biometrics are supported. */
    MULTIPLE_OR_GENERIC,

    /** Could not be determined or is not applicable. */
    UNKNOWN
}
