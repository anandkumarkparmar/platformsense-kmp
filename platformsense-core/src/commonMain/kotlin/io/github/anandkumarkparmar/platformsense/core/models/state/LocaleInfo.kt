package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the current device locale and regional data.
 *
 * Used to adapt content, formatting, and time presentation to regional preferences.
 *
 * @property languageTag The BCP-47 language tag (e.g. "en-US", "ja-JP").
 *                       May be empty if the platform cannot determine it.
 * @property language The ISO 639 language code (e.g. "en", "ja").
 * @property country The ISO 3166 country/region code (e.g. "US", "JP").
 * @property is24HourFormat True if the user prefers 24-hour time formatting, false for 12-hour.
 */
data class LocaleInfo(
    val languageTag: String = "",
    val language: String = "",
    val country: String = "",
    val is24HourFormat: Boolean = false
)
