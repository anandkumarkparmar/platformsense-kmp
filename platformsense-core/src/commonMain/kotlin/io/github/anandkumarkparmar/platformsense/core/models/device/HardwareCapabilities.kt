package io.github.anandkumarkparmar.platformsense.core.models.device

/**
 * Represents the hardware capabilities of the device.
 *
 * Use for feature gating (e.g. show NFC features only if NFC is available)
 * and adaptive behavior based on available sensors.
 *
 * @property hasCamera True if the device has any rear-facing camera.
 * @property hasFrontCamera True if the device has a front-facing camera.
 * @property hasNfc True if the device has NFC hardware.
 * @property hasGps True if the device has GPS hardware.
 * @property hasBluetooth True if the device has Bluetooth hardware.
 * @property hasAccelerometer True if the device has an accelerometer sensor.
 * @property hasGyroscope True if the device has a gyroscope sensor.
 * @property hasMagnetometer True if the device has a magnetometer (compass) sensor.
 * @property hasBarometer True if the device has a barometer (altimeter) sensor.
 * @property hasVibrator True if the device has a vibration motor.
 * @property hasHaptics True if the device supports advanced haptic feedback.
 */
public data class HardwareCapabilities(
    val hasCamera: Boolean = false,
    val hasFrontCamera: Boolean = false,
    val hasNfc: Boolean = false,
    val hasGps: Boolean = false,
    val hasBluetooth: Boolean = false,
    val hasAccelerometer: Boolean = false,
    val hasGyroscope: Boolean = false,
    val hasMagnetometer: Boolean = false,
    val hasBarometer: Boolean = false,
    val hasVibrator: Boolean = false,
    val hasHaptics: Boolean = false
)
