package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceClass
import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIUserInterfaceIdiomPhone
import platform.UIKit.UIUserInterfaceIdiomTV
import platform.posix.uname
import platform.posix.utsname

@OptIn(ExperimentalForeignApi::class)
class IosDeviceProvider : DeviceProvider {

    override fun current(): DeviceInfo = getDeviceInfo()

    // Device info rarely changes during a session
    override fun flow(): Flow<DeviceInfo> = flowOf(current())

    private fun getDeviceInfo(): DeviceInfo {
        val uiDevice = UIDevice.currentDevice

        val deviceClass = when (uiDevice.userInterfaceIdiom) {
            UIUserInterfaceIdiomPhone -> DeviceClass.PHONE
            UIUserInterfaceIdiomPad -> DeviceClass.TABLET
            UIUserInterfaceIdiomTV -> DeviceClass.TV
            else -> DeviceClass.UNKNOWN
        }

        val osName = uiDevice.systemName
        val osVersion = uiDevice.systemVersion
        val manufacturer = "Apple"
        val model = getDeviceModel()

        return DeviceInfo(
            deviceClass = deviceClass,
            osName = osName,
            osVersion = osVersion,
            manufacturer = manufacturer,
            model = model
        )
    }

    private fun getDeviceModel(): String = memScoped {
        val systemInfo = alloc<utsname>()
        uname(systemInfo.ptr)
        systemInfo.machine.toKString()
    }
}
