package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Foundation.NSCurrentLocaleDidChangeNotification
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

class IosLocaleProvider : LocaleProvider {

    override fun current(): LocaleInfo = getLocaleInfo()

    override fun flow(): Flow<LocaleInfo> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        val observer = center.addObserverForName(
            name = NSCurrentLocaleDidChangeNotification,
            `object` = null,
            queue = queue
        ) { _ ->
            trySend(getLocaleInfo())
        }

        trySend(current())

        awaitClose {
            center.removeObserver(observer)
        }
    }.distinctUntilChanged()

    private fun getLocaleInfo(): LocaleInfo {
        val locale = NSLocale.currentLocale
        val language = locale.languageCode
        val country = locale.countryCode ?: ""
        val languageTag = "${language}${if (country.isNotEmpty()) "-$country" else ""}"

        val formatter = NSDateFormatter()
        formatter.locale = locale
        formatter.dateStyle = NSDateFormatterNoStyle
        formatter.timeStyle = NSDateFormatterShortStyle
        val formatString = formatter.dateFormat ?: ""
        val is24HourFormat = formatString.contains("H") || formatString.contains("k")

        return LocaleInfo(
            languageTag = languageTag,
            language = language,
            country = country,
            is24HourFormat = is24HourFormat
        )
    }
}
