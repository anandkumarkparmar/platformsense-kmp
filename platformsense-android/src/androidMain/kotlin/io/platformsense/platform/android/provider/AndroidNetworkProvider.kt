package io.platformsense.platform.android.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import io.platformsense.core.provider.NetworkProvider
import io.platformsense.domain.NetworkType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [NetworkProvider] using [ConnectivityManager].
 *
 * Maps active network transport and metered capability to [NetworkType].
 * Uses [ConnectivityManager.getActiveNetwork] and [NetworkCapabilities] on API 23+.
 */
class AndroidNetworkProvider(private val context: Context) : NetworkProvider {

    private val connectivityManager: ConnectivityManager
        get() = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun current(): NetworkType = mapToNetworkType(connectivityManager.activeNetwork)

    override fun flow(): Flow<NetworkType> = callbackFlow {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            trySend(current())
            close()
            return@callbackFlow
        }
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities,) {
                trySend(mapToNetworkType(network))
            }
            override fun onLost(network: Network) {
                trySend(mapToNetworkType(connectivityManager.activeNetwork))
            }
        }
        connectivityManager.registerDefaultNetworkCallback(callback)
        trySend(current())
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()

    private fun mapToNetworkType(network: Network?): NetworkType {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return NetworkType.UNKNOWN
        if (network == null) return NetworkType.NONE
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.UNKNOWN
        val isMetered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                if (isMetered) NetworkType.METERED else NetworkType.WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                if (isMetered) NetworkType.METERED else NetworkType.CELLULAR
            caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->
                if (isMetered) NetworkType.METERED else NetworkType.WIFI
            else -> NetworkType.UNKNOWN
        }
    }
}
