package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import io.github.anandkumarkparmar.platformsense.core.models.state.DisplayInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.ScreenOrientation
import io.github.anandkumarkparmar.platformsense.core.provider.DisplayProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [DisplayProvider].
 *
 * Uses [android.util.DisplayMetrics] for density and pixel dimensions,
 * [Configuration] for dp dimensions and orientation, and [android.view.Display]
 * for refresh rate. Cutout detection uses [android.view.DisplayCutout] (API 28+).
 */
internal class AndroidDisplayProvider(private val context: Context) : DisplayProvider {

    override fun current(): DisplayInfo = buildDisplayInfo()

    override fun flow(): Flow<DisplayInfo> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(buildDisplayInfo())
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    @Suppress("DEPRECATION")
    private fun buildDisplayInfo(): DisplayInfo {
        val resources = context.resources
        val metrics = resources.displayMetrics
        val config = resources.configuration

        val orientation = when (config.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> ScreenOrientation.PORTRAIT
            Configuration.ORIENTATION_LANDSCAPE -> ScreenOrientation.LANDSCAPE
            else -> ScreenOrientation.UNKNOWN
        }

        val refreshRate = getRefreshRate()
        val hasCutout = hasCutout()

        return DisplayInfo(
            widthDp = config.screenWidthDp,
            heightDp = config.screenHeightDp,
            widthPixels = metrics.widthPixels,
            heightPixels = metrics.heightPixels,
            density = metrics.density,
            refreshRate = refreshRate,
            orientation = orientation,
            hasCutout = hasCutout
        )
    }

    @Suppress("DEPRECATION")
    private fun getRefreshRate(): Float? = try {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        windowManager?.defaultDisplay?.refreshRate
    } catch (_: Exception) {
        null
    }

    private fun hasCutout(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return false
        return try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = windowManager?.currentWindowMetrics
                windowMetrics?.windowInsets?.displayCutout != null
            } else {
                @Suppress("DEPRECATION")
                val display = windowManager?.defaultDisplay
                // DisplayCutout is only accessible from a window, fall back to false
                // for non-activity contexts prior to API 30.
                display != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
            }
        } catch (_: Exception) {
            false
        }
    }
}
