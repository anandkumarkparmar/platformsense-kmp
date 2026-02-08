package io.platformsense.domain

/**
 * Represents the current device power / battery state.
 *
 * Used for environment-aware behavior such as disabling animations in low power mode
 * or reducing background activity when the device is not charging.
 */
enum class PowerState {
    /** Normal power state; device is not in a power-saving mode. */
    NORMAL,

    /**
     * Device is in low power / power saver mode.
     * Consider reducing animations and non-essential work.
     */
    LOW_POWER,

    /** Device is charging (AC or wireless). */
    CHARGING,

    /**
     * Power state could not be determined or is not applicable.
     * Used when the platform does not support or cannot provide this signal.
     */
    UNKNOWN
}
