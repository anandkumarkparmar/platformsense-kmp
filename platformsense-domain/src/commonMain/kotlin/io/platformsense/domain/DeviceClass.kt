package io.platformsense.domain

/**
 * Represents the device form factor or class.
 *
 * Used to adapt layouts and UX (e.g. phone vs tablet) and to make
 * capability assumptions appropriate to the device type.
 */
enum class DeviceClass {
    /** Phone or similar small-form-factor device. */
    PHONE,

    /** Tablet or similar large-form-factor device. */
    TABLET,

    /** Desktop or laptop. */
    DESKTOP,

    /** TV or set-top device. */
    TV,

    /**
     * Device class could not be determined or is not applicable.
     * Used when the platform does not support or cannot provide this signal.
     */
    UNKNOWN
}
