package io.platformsense.sample

import android.app.Application
import io.platformsense.core.PlatformSense
import io.platformsense.platform.android.AndroidPlatformSenseWiring

/**
 * Application class that initializes PlatformSense with Android providers.
 */
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformSense.initialize(AndroidPlatformSenseWiring(this))
    }
}
