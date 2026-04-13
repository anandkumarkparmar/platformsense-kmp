package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the current network connectivity state.
 *
 * Used for environment-aware behavior such as reducing image quality on metered networks
 * or adapting content delivery based on connectivity.
 *
 * @property type The type of network transport (WiFi, Cellular, Ethernet, None, Unknown).
 * @property isConnected True if the device has an active network connection.
 * @property isMetered True if the active network is metered (e.g. cellular or limited Wi-Fi).
 *                    May be used to reduce data usage (e.g. lower image quality).
 * @property isRoaming True if the active network is roaming (applicable mostly to cellular).
 */
data class NetworkInfo(
    val type: NetworkType = NetworkType.UNKNOWN,
    val isConnected: Boolean = false,
    val isMetered: Boolean = false,
    val isRoaming: Boolean = false
)

/**
 * Represents the type of network transport.
 */
enum class NetworkType {
    /** Device is connected via Wi-Fi. */
    WIFI,

    /** Device is connected via cellular (mobile) network. */
    CELLULAR,

    /** Device is connected via Ethernet natively. */
    ETHERNET,

    /** Device has no network connectivity. */
    NONE,

    /**
     * Network type could not be determined or is not applicable.
     */
    UNKNOWN
}
