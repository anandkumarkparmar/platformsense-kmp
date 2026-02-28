package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceClass
import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
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

    override fun current(): DeviceInfo = mapToDeviceInfo(context.resources.configuration)

    override fun flow(): Flow<DeviceInfo> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(mapToDeviceInfo(newConfig))
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    private fun mapToDeviceInfo(config: Configuration): DeviceInfo {
        val deviceClass = when {
            config.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION -> DeviceClass.TV
            config.smallestScreenWidthDp >= 600 -> DeviceClass.TABLET
            config.smallestScreenWidthDp > 0 -> DeviceClass.PHONE
            else -> DeviceClass.UNKNOWN
        }

        return DeviceInfo(
            deviceClass = deviceClass,
            osName = "Android",
            osVersion = Build.VERSION.RELEASE ?: "Unknown",
            manufacturer = Build.MANUFACTURER ?: "Unknown",
            model = Build.MODEL ?: "Unknown"
        )
    }
}
