package io.github.anandkumarkparmar.platformsense.sample

import android.app.Application
import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.platform.android.AndroidPlatformSenseWiring

/**
 * Application class that initializes PlatformSense with Android providers.
 */
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PlatformSense.initialize(AndroidPlatformSenseWiring(this))
    }
}
