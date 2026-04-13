package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerState
import io.github.anandkumarkparmar.platformsense.core.models.state.ThermalState
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
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

    private val powerManager: PowerManager =
        context.getSystemService(Context.POWER_SERVICE) as PowerManager

    override fun current(): PowerInfo = mapToPowerInfo(batteryIntent = null)

    override fun flow(): Flow<PowerInfo> = callbackFlow {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Pass the received battery intent directly to avoid a redundant sticky-intent lookup.
                val batteryIntent = if (intent.action == Intent.ACTION_BATTERY_CHANGED) intent else null
                trySend(mapToPowerInfo(batteryIntent = batteryIntent))
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
        trySend(mapToPowerInfo(batteryIntent = null))
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged()

    private fun mapToPowerInfo(batteryIntent: Intent?): PowerInfo {
        val status = if (powerManager.isPowerSaveMode) PowerState.LOW_POWER else PowerState.NORMAL

        // Use the already-received intent when available (e.g. from ACTION_BATTERY_CHANGED callback),
        // otherwise fall back to querying the sticky intent for one-shot calls like current().
        val resolvedIntent = batteryIntent
            ?: context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        var batteryLevel: Float? = null
        var isCharging = false

        if (resolvedIntent != null) {
            val intentLevel = resolvedIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val intentScale = resolvedIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

            if (intentLevel != -1 && intentScale != -1) {
                batteryLevel = intentLevel / intentScale.toFloat()
            }

            val plugged = resolvedIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            isCharging = plugged == BatteryManager.BATTERY_PLUGGED_AC ||
                plugged == BatteryManager.BATTERY_PLUGGED_USB ||
                plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }

        val thermalState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            when (powerManager.currentThermalStatus) {
                PowerManager.THERMAL_STATUS_NONE -> ThermalState.NORMAL
                PowerManager.THERMAL_STATUS_LIGHT, PowerManager.THERMAL_STATUS_MODERATE -> ThermalState.FAIR
                PowerManager.THERMAL_STATUS_SEVERE -> ThermalState.SERIOUS
                PowerManager.THERMAL_STATUS_CRITICAL,
                PowerManager.THERMAL_STATUS_EMERGENCY,
                PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalState.CRITICAL
                else -> ThermalState.UNKNOWN
            }
        } else {
            ThermalState.UNKNOWN
        }

        return PowerInfo(
            status = status,
            batteryLevel = batteryLevel,
            isCharging = isCharging,
            thermalState = thermalState
        )
    }
}
