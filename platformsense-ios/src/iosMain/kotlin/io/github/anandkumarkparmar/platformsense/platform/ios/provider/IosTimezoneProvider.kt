package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.TimezoneInfo
import io.github.anandkumarkparmar.platformsense.core.provider.TimezoneProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSSystemTimeZoneDidChangeNotification
import platform.Foundation.NSTimeZone
import platform.Foundation.abbreviation
import platform.Foundation.defaultTimeZone
import platform.Foundation.resetSystemTimeZone
import platform.Foundation.secondsFromGMT

class IosTimezoneProvider : TimezoneProvider {

    override fun current(): TimezoneInfo = getTimezoneInfo()

    override fun flow(): Flow<TimezoneInfo> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        val observer = center.addObserverForName(
            name = NSSystemTimeZoneDidChangeNotification,
            `object` = null,
            queue = queue
        ) { _ ->
            NSTimeZone.resetSystemTimeZone()
            trySend(getTimezoneInfo())
        }

        trySend(current())

        awaitClose {
            center.removeObserver(observer)
        }
    }.distinctUntilChanged()

    private fun getTimezoneInfo(): TimezoneInfo {
        val timeZone = NSTimeZone.defaultTimeZone
        val id = timeZone.name
        val displayName = timeZone.abbreviation ?: id

        return TimezoneInfo(
            id = id,
            displayName = displayName,
            offsetMillis = (timeZone.secondsFromGMT.toInt()) * 1000
        )
    }
}
