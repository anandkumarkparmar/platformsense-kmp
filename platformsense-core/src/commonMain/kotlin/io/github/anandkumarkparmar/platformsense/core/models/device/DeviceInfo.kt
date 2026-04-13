package io.github.anandkumarkparmar.platformsense.core.models.device

/**
 * Represents the current device identity and hardware form factor.
 *
 * Used to adapt layouts and UX (e.g. phone vs tablet) and to log or
 * perform capability assumptions appropriate to the device type and OS version.
 *
 * @property deviceClass The form factor of the device (Phone, Tablet, Desktop, TV).
 * @property osName The name of the operating system (e.g. "Android", "iOS").
 * @property osVersion The version of the operating system (e.g. "14", "17.4").
 * @property manufacturer The manufacturer of the device hardware (e.g. "Samsung", "Apple"). Empty if unknown.
 * @property model The model of the device hardware (e.g. "SM-G998B", "iPhone 15 Pro"). Empty if unknown.
 */
public data class DeviceInfo(
    val deviceClass: DeviceClass = DeviceClass.UNKNOWN,
    val osName: String = "",
    val osVersion: String = "",
    val manufacturer: String = "",
    val model: String = ""
)

/**
 * Represents the device form factor or class.
 */
public enum class DeviceClass {
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
     */
    UNKNOWN
}
