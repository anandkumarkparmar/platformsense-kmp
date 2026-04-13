package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the current system appearance / theme state.
 *
 * Used to adapt UI to the user's preferred color scheme without relying on
 * Compose or framework-level theme detection in shared business logic.
 *
 * @property isDarkMode True if the system is currently in dark mode.
 * @property isDynamicColorAvailable True if the device supports dynamic color / Material You theming (Android 12+).
 *                                   Always false on iOS.
 */
public data class AppearanceInfo(val isDarkMode: Boolean = false, val isDynamicColorAvailable: Boolean = false)
