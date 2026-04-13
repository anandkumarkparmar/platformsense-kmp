package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the current device power and battery information.
 *
 * Used for environment-aware behavior such as disabling animations in low power mode
 * or reducing background activity when the device is not charging.
 *
 * @property status The overall power status (e.g. Normal, Low Power).
 * @property batteryLevel The current battery level as a percentage from 0.0 to 1.0. Null if unknown.
 * @property isCharging True if the device is currently connected to power.
 * @property thermalState The current thermal state of the device (Normal, Fair, Serious, Critical).
 */
public data class PowerInfo(
    val status: PowerState = PowerState.UNKNOWN,
    val batteryLevel: Float? = null,
    val isCharging: Boolean = false,
    val thermalState: ThermalState = ThermalState.UNKNOWN,
)

/**
 * Represents the high-level power status of the device.
 */
public enum class PowerState {
    /** Normal power state; device is not in a power-saving mode. */
    NORMAL,

    /**
     * Device is in low power / power saver mode.
     * Consider reducing animations and non-essential work.
     */
    LOW_POWER,

    /**
     * Power status could not be determined or is not applicable.
     */
    UNKNOWN
}

/**
 * Represents the thermal state of the device.
 * Used to throttle performance-heavy operations.
 */
public enum class ThermalState {
    /** Normal operating temperature. */
    NORMAL,

    /** Elevated temperature; consider reducing non-essential work slightly. */
    FAIR,

    /** High temperature; reduce work immediately to avoid thermal throttling or shutdown. */
    SERIOUS,

    /** Critical temperature; device is near shutdown. Suspend all non-essential activity. */
    CRITICAL,

    /** Thermal state could not be determined. */
    UNKNOWN
}
