package io.platformsense.platform.android.provider

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import io.platformsense.core.provider.PowerProvider
import io.platformsense.domain.PowerState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Android implementation of [PowerProvider] using [PowerManager] and [BatteryManager].
 *
 * Maps power save mode and charging status to [PowerState].
 * Requires API 23+ (minSdk).
 */
class AndroidPowerProvider(private val context: Context) : PowerProvider {

    private val powerManager: PowerManager
        get() = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private val batteryManager: BatteryManager
        get() = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    override fun current(): PowerState = mapToPowerState()

    override fun flow(): Flow<PowerState> = callbackFlow {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(mapToPowerState())
            }
        }
        val filter = IntentFilter().apply {
            addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        // RECEIVER_NOT_EXPORTED flag required for API 33+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
        trySend(mapToPowerState())
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()

    private fun mapToPowerState(): PowerState {
        if (powerManager.isPowerSaveMode) return PowerState.LOW_POWER
        val status = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        ) {
            return PowerState.CHARGING
        }
        return PowerState.NORMAL
    }
}
