package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.app.ActivityManager
import android.content.Context
import io.github.anandkumarkparmar.platformsense.core.models.state.MemoryInfo
import io.github.anandkumarkparmar.platformsense.core.provider.MemoryProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidMemoryProvider(private val context: Context) : MemoryProvider {

    override fun current(): MemoryInfo = getMemoryInfo()

    override fun flow(): Flow<MemoryInfo> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {}

            override fun onLowMemory() {
                trySend(getMemoryInfo().copy(isLowMemory = true))
            }

            override fun onTrimMemory(level: Int) {
                if (level >= android.content.ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
                    trySend(getMemoryInfo().copy(isLowMemory = true))
                }
            }
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    private fun getMemoryInfo(): MemoryInfo {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memInfo)
        return MemoryInfo(
            totalRamBytes = memInfo.totalMem,
            availableRamBytes = memInfo.availMem,
            isLowMemory = memInfo.lowMemory
        )
    }
}
