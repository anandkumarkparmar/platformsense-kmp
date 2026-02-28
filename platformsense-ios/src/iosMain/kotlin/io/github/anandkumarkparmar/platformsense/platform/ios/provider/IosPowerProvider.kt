package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerState
import io.github.anandkumarkparmar.platformsense.core.models.environment.ThermalState
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSProcessInfoPowerStateDidChangeNotification
import platform.Foundation.NSProcessInfoThermalState
import platform.Foundation.NSProcessInfoThermalStateDidChangeNotification
import platform.Foundation.isLowPowerModeEnabled
import platform.Foundation.thermalState
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState
import platform.UIKit.UIDeviceBatteryStateDidChangeNotification

class IosPowerProvider : PowerProvider {

    init {
        UIDevice.currentDevice.batteryMonitoringEnabled = true
    }

    override fun current(): PowerInfo = getPowerInfo()

    override fun flow(): Flow<PowerInfo> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val queue = platform.Foundation.NSOperationQueue.mainQueue

        val pwrStateObserver = center.addObserverForName(
            name = NSProcessInfoPowerStateDidChangeNotification,
            `object` = null,
            queue = queue
        ) { _ ->
            trySend(getPowerInfo())
        }

        val batteryObserver = center.addObserverForName(
            name = UIDeviceBatteryStateDidChangeNotification,
            `object` = null,
            queue = queue
        ) { _ ->
            trySend(getPowerInfo())
        }

        val thermalObserver = center.addObserverForName(
            name = NSProcessInfoThermalStateDidChangeNotification,
            `object` = null,
            queue = queue
        ) { _ ->
            trySend(getPowerInfo())
        }

        trySend(current())

        awaitClose {
            center.removeObserver(pwrStateObserver)
            center.removeObserver(batteryObserver)
            center.removeObserver(thermalObserver)
        }
    }.distinctUntilChanged()

    private fun getPowerInfo(): PowerInfo {
        val uiDevice = UIDevice.currentDevice
        val batteryLevelResult = uiDevice.batteryLevel
        val batteryLevel = if (batteryLevelResult >= 0f) batteryLevelResult else null

        val isCharging = when (uiDevice.batteryState) {
            UIDeviceBatteryState.UIDeviceBatteryStateCharging,
            UIDeviceBatteryState.UIDeviceBatteryStateFull -> true
            else -> false
        }

        val status = if (NSProcessInfo.processInfo.isLowPowerModeEnabled()) {
            PowerState.LOW_POWER
        } else {
            PowerState.NORMAL
        }

        val thermalStateRaw = NSProcessInfo.processInfo.thermalState
        val thermalState = when (thermalStateRaw) {
            NSProcessInfoThermalState.NSProcessInfoThermalStateNominal -> ThermalState.NORMAL
            NSProcessInfoThermalState.NSProcessInfoThermalStateFair -> ThermalState.FAIR
            NSProcessInfoThermalState.NSProcessInfoThermalStateSerious -> ThermalState.SERIOUS
            NSProcessInfoThermalState.NSProcessInfoThermalStateCritical -> ThermalState.CRITICAL
            else -> ThermalState.UNKNOWN
        }

        return PowerInfo(
            status = status,
            batteryLevel = batteryLevel,
            isCharging = isCharging,
            thermalState = thermalState
        )
    }
}
