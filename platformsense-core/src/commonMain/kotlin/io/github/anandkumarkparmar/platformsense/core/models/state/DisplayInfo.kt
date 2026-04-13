package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents display and screen information.
 *
 * Combines both static display properties (density, resolution) and runtime-changing
 * properties (orientation, current dimensions in dp). Use for adaptive layouts,
 * image resolution decisions, and orientation-aware UI.
 *
 * @property widthDp Current screen width in density-independent pixels.
 * @property heightDp Current screen height in density-independent pixels.
 * @property widthPixels Screen width in physical pixels.
 * @property heightPixels Screen height in physical pixels.
 * @property density Screen density scale factor (e.g. 2.0 for @2x, 3.0 for @3x).
 * @property refreshRate Screen refresh rate in Hz, if available. Null if unknown.
 * @property orientation Current screen orientation.
 * @property hasCutout True if the display has a notch or cutout area.
 */
data class DisplayInfo(
    val widthDp: Int = 0,
    val heightDp: Int = 0,
    val widthPixels: Int = 0,
    val heightPixels: Int = 0,
    val density: Float = 1.0f,
    val refreshRate: Float? = null,
    val orientation: ScreenOrientation = ScreenOrientation.UNKNOWN,
    val hasCutout: Boolean = false
)

/**
 * Represents the current screen orientation.
 */
enum class ScreenOrientation {
    /** Device is in portrait (vertical) orientation. */
    PORTRAIT,

    /** Device is in landscape (horizontal) orientation. */
    LANDSCAPE,

    /** Orientation could not be determined. */
    UNKNOWN
}
