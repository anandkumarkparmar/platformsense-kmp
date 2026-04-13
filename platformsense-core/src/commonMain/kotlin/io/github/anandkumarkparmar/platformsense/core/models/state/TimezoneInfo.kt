package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the current device timezone settings.
 *
 * Used to adapt time and date presentations or schedule-related UI properly.
 *
 * @property id The IANA timezone identifier (e.g. "America/New_York", "Asia/Tokyo").
 *              May be empty if the platform cannot determine it.
 * @property displayName The localized display name of the timezone (e.g. "Eastern Standard Time").
 * @property offsetMillis The offset from UTC in milliseconds at the current moment, taking daylight savings into account.
 */
data class TimezoneInfo(
    val id: String = "",
    val displayName: String = "",
    val offsetMillis: Int = 0
)
