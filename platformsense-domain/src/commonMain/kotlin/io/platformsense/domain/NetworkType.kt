package io.platformsense.domain

/**
 * Represents the current network connectivity type.
 *
 * Used for environment-aware behavior such as reducing image quality on metered networks
 * or adapting content delivery based on connectivity.
 */
enum class NetworkType {
    /** Device is connected via Wi-Fi. */
    WIFI,

    /** Device is connected via cellular (mobile) network. */
    CELLULAR,

    /** Device has no network connectivity. */
    NONE,

    /**
     * Network type is metered (e.g. cellular or limited Wi-Fi).
     * May be used to reduce data usage (e.g. lower image quality).
     */
    METERED,

    /**
     * Network type could not be determined or is not applicable.
     * Used when the platform does not support or cannot provide this signal.
     */
    UNKNOWN
}
