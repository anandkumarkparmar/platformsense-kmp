package io.platformsense.platform.android.provider

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import io.platformsense.core.provider.TimezoneProvider
import java.util.TimeZone
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [TimezoneProvider] using [TimeZone.getDefault].
 *
 * Returns IANA timezone ID. Emits again when the system timezone changes.
 */
class AndroidTimezoneProvider(private val context: Context) : TimezoneProvider {

    override fun current(): String = TimeZone.getDefault().id

    override fun flow(): Flow<String> = callbackFlow {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(TimeZone.getDefault().id)
            }
        }
        val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        trySend(current())
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()
}
