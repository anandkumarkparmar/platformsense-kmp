package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the current accessibility settings and state.
 *
 * Use for adaptive UX: reduce animations when reduce motion is enabled,
 * increase touch targets when screen reader is active, etc.
 *
 * @property isScreenReaderEnabled True if a screen reader (TalkBack/VoiceOver) is active.
 * @property isBoldTextEnabled True if the user has enabled bold text. Always false on Android.
 * @property fontScale The user's preferred font scale factor (1.0 = default).
 * @property isReduceMotionEnabled True if the user has enabled reduce motion / disable animations.
 * @property isHighContrastEnabled True if the user has enabled high contrast mode.
 * @property isColorInversionEnabled True if the user has enabled color inversion.
 */
data class AccessibilityInfo(
    val isScreenReaderEnabled: Boolean = false,
    val isBoldTextEnabled: Boolean = false,
    val fontScale: Float = 1.0f,
    val isReduceMotionEnabled: Boolean = false,
    val isHighContrastEnabled: Boolean = false,
    val isColorInversionEnabled: Boolean = false
)
