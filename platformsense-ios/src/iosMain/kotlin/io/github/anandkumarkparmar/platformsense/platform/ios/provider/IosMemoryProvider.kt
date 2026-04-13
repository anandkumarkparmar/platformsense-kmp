package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.MemoryInfo
import io.github.anandkumarkparmar.platformsense.core.provider.MemoryProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIApplicationDidReceiveMemoryWarningNotification

class IosMemoryProvider : MemoryProvider {

    override fun current(): MemoryInfo = getMemoryInfo()

    override fun flow(): Flow<MemoryInfo> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        val observer = center.addObserverForName(
            name = UIApplicationDidReceiveMemoryWarningNotification,
            `object` = null,
            queue = queue,
        ) { _ ->
            trySend(getMemoryInfo().copy(isLowMemory = true))
        }

        trySend(current())

        awaitClose {
            center.removeObserver(observer)
        }
    }.distinctUntilChanged()

    private fun getMemoryInfo(): MemoryInfo {
        val totalRam = NSProcessInfo.processInfo.physicalMemory.toLong()
        return MemoryInfo(
            totalRamBytes = totalRam,
            availableRamBytes = null,
            isLowMemory = false,
        )
    }
}
