package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Vibrator
import io.github.anandkumarkparmar.platformsense.core.models.device.HardwareCapabilities
import io.github.anandkumarkparmar.platformsense.core.provider.HardwareCapabilitiesProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AndroidHardwareCapabilitiesProvider(private val context: Context) : HardwareCapabilitiesProvider {

    @Suppress("DEPRECATION")
    override fun current(): HardwareCapabilities {
        val pm = context.packageManager
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        return HardwareCapabilities(
            hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY),
            hasFrontCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT),
            hasNfc = pm.hasSystemFeature(PackageManager.FEATURE_NFC),
            hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS),
            hasBluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH),
            hasAccelerometer = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER),
            hasGyroscope = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE),
            hasMagnetometer = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS),
            hasBarometer = pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER),
            hasVibrator = vibrator?.hasVibrator() ?: false,
            hasHaptics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.hasAmplitudeControl() ?: false
            } else {
                false
            }
        )
    }

    override fun flow(): Flow<HardwareCapabilities> = flowOf(current())
}
