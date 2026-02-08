package io.platformsense.platform.android.provider

import android.content.Context
import android.content.res.Configuration
import io.platformsense.core.provider.DeviceProvider
import io.platformsense.domain.DeviceClass
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [DeviceProvider] using [Configuration] and [Context].
 *
 * Maps screen size (smallestScreenWidthDp) and UI mode to [DeviceClass].
 * Phone &lt; 600dp, Tablet ≥ 600dp, TV via UI_MODE_TYPE_TELEVISION.
 */
class AndroidDeviceProvider(private val context: Context) : DeviceProvider {

    override fun current(): DeviceClass = mapToDeviceClass(context.resources.configuration)

    override fun flow(): Flow<DeviceClass> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(mapToDeviceClass(newConfig))
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    private fun mapToDeviceClass(config: Configuration): DeviceClass {
        if (config.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION) {
            return DeviceClass.TV
        }
        val smallestScreenWidthDp = config.smallestScreenWidthDp
        return when {
            smallestScreenWidthDp >= 600 -> DeviceClass.TABLET
            smallestScreenWidthDp > 0 -> DeviceClass.PHONE
            else -> DeviceClass.UNKNOWN
        }
    }
}
