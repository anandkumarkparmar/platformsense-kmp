package io.github.anandkumarkparmar.platformsense.core.models.snapshot

import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.TimezoneInfo

/**
 * Immutable snapshot of the current environment signals.
 *
 * Aggregates network, power, device, locale, and timezone into a single
 * value object for snapshot-style queries. Use [EnvironmentSnapshot] when you need
 * a point-in-time view of the environment (e.g. for one-off decisions or UI updates).
 *
 * For reactive updates, use the environment flow provided by the core layer.
 *
 * @property networkInfo Current network connectivity state.
 * @property powerInfo Current device power / battery info.
 * @property deviceInfo Device identity and form factor information.
 * @property localeInfo Current locale and formatting preferences.
 * @property timezoneInfo Current timezone information.
 */
data class EnvironmentSnapshot(
    val networkInfo: NetworkInfo = NetworkInfo(),
    val powerInfo: PowerInfo = PowerInfo(),
    val deviceInfo: DeviceInfo = DeviceInfo(),
    val localeInfo: LocaleInfo = LocaleInfo(),
    val timezoneInfo: TimezoneInfo = TimezoneInfo()
)
