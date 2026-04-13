package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.HardwareCapabilities
import io.github.anandkumarkparmar.platformsense.core.provider.HardwareCapabilitiesProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.CoreLocation.CLLocationManager
import platform.CoreMotion.CMMotionManager
import platform.UIKit.UIDevice

class IosHardwareCapabilitiesProvider : HardwareCapabilitiesProvider {

    override fun current(): HardwareCapabilities {
        val motionManager = CMMotionManager()
        val isIPhone = UIDevice.currentDevice.model.contains("iPhone")

        return HardwareCapabilities(
            hasCamera = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) != null,
            hasFrontCamera = isIPhone,
            hasNfc = isIPhone,
            hasGps = CLLocationManager.locationServicesEnabled(),
            hasBluetooth = true,
            hasAccelerometer = motionManager.accelerometerAvailable,
            hasGyroscope = motionManager.gyroAvailable,
            hasMagnetometer = motionManager.magnetometerAvailable,
            hasBarometer = true,
            hasVibrator = isIPhone,
            hasHaptics = isIPhone,
        )
    }

    override fun flow(): Flow<HardwareCapabilities> = flowOf(current())
}
