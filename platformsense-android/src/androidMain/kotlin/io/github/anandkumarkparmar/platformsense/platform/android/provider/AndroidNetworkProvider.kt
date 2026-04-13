package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkType
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [NetworkProvider] using [ConnectivityManager].
 *
 * Maps active network transport and metered capability to [NetworkType].
 * Requires API 23+ (minSdk).
 */
internal class AndroidNetworkProvider(private val context: Context) : NetworkProvider {

    private val connectivityManager: ConnectivityManager
        get() = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun current(): NetworkInfo = mapToNetworkInfo(connectivityManager.activeNetwork)

    override fun flow(): Flow<NetworkInfo> = callbackFlow {
        // registerDefaultNetworkCallback requires API 24+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            trySend(current())
            close()
            return@callbackFlow
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                trySend(mapToNetworkInfo(network))
            }

            override fun onLost(network: Network) {
                trySend(mapToNetworkInfo(connectivityManager.activeNetwork))
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(current())
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun mapToNetworkInfo(network: Network?): NetworkInfo {
        if (network == null) return NetworkInfo(isConnected = false)
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return NetworkInfo(isConnected = false)

        // Sometimes the network is present but we can't validate it has internet capability.
        // We'll trust that if activeNetwork returned it, it's at least conceptually connected,
        // but checking NET_CAPABILITY_INTERNET makes it safer if available.
        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (!hasInternet) return NetworkInfo(isConnected = false)

        val isMetered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

        // Android 28+ check for roaming on cellular
        val isRoaming = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
        } else {
            false // Fallback for API 23-27 where roaming isn't as easily checked from NetworkCapabilities
        }

        val type = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            else -> NetworkType.UNKNOWN
        }

        return NetworkInfo(
            type = type,
            isConnected = true,
            isMetered = isMetered,
            isRoaming = isRoaming
        )
    }
}
