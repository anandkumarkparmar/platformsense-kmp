package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkType
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Network.nw_interface_type_cellular
import platform.Network.nw_interface_type_wifi
import platform.Network.nw_interface_type_wired
import platform.Network.nw_path_get_status
import platform.Network.nw_path_is_expensive
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.Network.nw_path_uses_interface_type
import platform.darwin.dispatch_get_main_queue

internal class IosNetworkProvider : NetworkProvider {

    // Cached last-known network info; updated by the flow. Provides best-effort synchronous access.
    private var cachedNetworkInfo: NetworkInfo = NetworkInfo()

    override fun current(): NetworkInfo = cachedNetworkInfo

    override fun flow(): Flow<NetworkInfo> = callbackFlow {
        val monitor = nw_path_monitor_create()

        nw_path_monitor_set_update_handler(monitor) { path ->
            val info = if (path == null) {
                NetworkInfo(isConnected = false, isMetered = false, isRoaming = false, type = NetworkType.UNKNOWN)
            } else {
                val isConnected = nw_path_get_status(path) == nw_path_status_satisfied
                val isMetered = nw_path_is_expensive(path)
                val type = when {
                    nw_path_uses_interface_type(path, nw_interface_type_wifi) -> NetworkType.WIFI
                    nw_path_uses_interface_type(path, nw_interface_type_cellular) -> NetworkType.CELLULAR
                    nw_path_uses_interface_type(path, nw_interface_type_wired) -> NetworkType.ETHERNET
                    else -> NetworkType.UNKNOWN
                }
                NetworkInfo(type = type, isConnected = isConnected, isMetered = isMetered, isRoaming = false)
            }
            cachedNetworkInfo = info
            trySend(info)
        }

        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)

        awaitClose {
            nw_path_monitor_cancel(monitor)
        }
    }.distinctUntilChanged()
}
