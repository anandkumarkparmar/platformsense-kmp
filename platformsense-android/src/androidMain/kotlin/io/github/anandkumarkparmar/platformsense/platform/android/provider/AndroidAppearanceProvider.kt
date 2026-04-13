package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import io.github.anandkumarkparmar.platformsense.core.models.state.AppearanceInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AppearanceProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [AppearanceProvider].
 *
 * Detects dark mode via [Configuration.uiMode] and dynamic color support
 * via API level (Android 12 / API 31+).
 */
class AndroidAppearanceProvider(private val context: Context) : AppearanceProvider {

    override fun current(): AppearanceInfo = mapToAppearanceInfo(context.resources.configuration)

    override fun flow(): Flow<AppearanceInfo> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(mapToAppearanceInfo(newConfig))
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    private fun mapToAppearanceInfo(config: Configuration): AppearanceInfo {
        val nightMode = config.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return AppearanceInfo(
            isDarkMode = nightMode == Configuration.UI_MODE_NIGHT_YES,
            isDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        )
    }
}
