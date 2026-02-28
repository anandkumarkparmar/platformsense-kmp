package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.text.format.DateFormat
import io.github.anandkumarkparmar.platformsense.core.models.environment.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
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

    override fun current(): LocaleInfo = mapToLocaleInfo(context.resources.configuration)

    override fun flow(): Flow<LocaleInfo> = callbackFlow {
        val callback = object : android.content.ComponentCallbacks2 {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(mapToLocaleInfo(newConfig))
            }

            override fun onLowMemory() {}
            override fun onTrimMemory(level: Int) {}
        }
        context.registerComponentCallbacks(callback)
        trySend(current())
        awaitClose { context.unregisterComponentCallbacks(callback) }
    }.distinctUntilChanged()

    private fun mapToLocaleInfo(config: Configuration): LocaleInfo {
        val defaultLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (config.locales.size() > 0) config.locales.get(0) else Locale.getDefault()
        } else {
            @Suppress("DEPRECATION")
            config.locale ?: Locale.getDefault()
        }

        return LocaleInfo(
            languageTag = if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.LOLLIPOP
            ) {
                defaultLocale.toLanguageTag()
            } else {
                defaultLocale.toString()
            },
            language = defaultLocale.language,
            country = defaultLocale.country,
            is24HourFormat = DateFormat.is24HourFormat(context)
        )
    }
}
