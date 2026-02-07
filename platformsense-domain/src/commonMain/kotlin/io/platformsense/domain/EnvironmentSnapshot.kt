package io.platformsense.domain

/**
 * Immutable snapshot of the current environment signals.
 *
 * Aggregates network, power, device, locale, and timezone into a single
 * value object for snapshot-style queries. Use [EnvironmentSnapshot] when you need
 * a point-in-time view of the environment (e.g. for one-off decisions or UI updates).
 *
 * For reactive updates, use the environment flow provided by the core layer.
 *
 * @property networkType Current network connectivity type.
 * @property powerState Current device power / battery state.
 * @property deviceClass Device form factor (phone, tablet, etc.).
 * @property locale Current locale identifier (e.g. "en_US"); empty if unknown.
 * @property timezone Current timezone identifier (e.g. "America/New_York"); empty if unknown.
 */
data class EnvironmentSnapshot(
    val networkType: NetworkType = NetworkType.UNKNOWN,
    val powerState: PowerState = PowerState.UNKNOWN,
    val deviceClass: DeviceClass = DeviceClass.UNKNOWN,
    val locale: String = "",
    val timezone: String = ""
)
