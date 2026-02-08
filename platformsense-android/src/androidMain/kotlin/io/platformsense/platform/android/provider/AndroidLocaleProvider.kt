package io.platformsense.platform.android.provider

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import io.platformsense.core.provider.LocaleProvider
import java.util.Locale
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [LocaleProvider] using [Configuration] and [Locale].
 *
 * Returns the first locale from the configuration (or default) in BCP 47 form.
 */
class AndroidLocaleProvider(private val context: Context) : LocaleProvider {

    override fun current(): String = getLocaleString(context.resources.configuration)

    override fun flow(): Flow<String> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(getLocaleString(newConfig))
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    private fun getLocaleString(config: Configuration): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        if (config.locales.size() > 0) config.locales.get(0).toLanguageTag() else ""
    } else {
        @Suppress("DEPRECATION")
        config.locale?.toLanguageTag() ?: Locale.getDefault().toLanguageTag()
    }
}
