package io.platformsense.sample

import android.content.Context
import io.platformsense.core.PlatformSense
import io.platformsense.platform.android.AndroidPlatformSenseWiring

/**
 * Android: initialize with real system providers.
 */
actual fun initPlatformSense() {
    val context = getApplicationContext()
    PlatformSense.initialize(AndroidPlatformSenseWiring(context))
}

private lateinit var appContext: Context
fun setPlatformSenseContext(context: Context) {
    appContext = context.applicationContext
}
fun getApplicationContext(): Context = appContext
