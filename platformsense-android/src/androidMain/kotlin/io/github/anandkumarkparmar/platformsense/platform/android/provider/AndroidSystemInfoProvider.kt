package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.os.Build
import io.github.anandkumarkparmar.platformsense.core.models.device.SystemInfo
import io.github.anandkumarkparmar.platformsense.core.provider.SystemInfoProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class AndroidSystemInfoProvider : SystemInfoProvider {

    override fun current(): SystemInfo {
        val securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else {
            null
        }

        val isEmulator = Build.FINGERPRINT.contains("generic", ignoreCase = true) ||
            Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MODEL.contains("Android SDK built for", ignoreCase = true) ||
            Build.MANUFACTURER.contains("Genymotion", ignoreCase = true) ||
            Build.PRODUCT.contains("sdk", ignoreCase = true)

        return SystemInfo(
            apiLevel = Build.VERSION.SDK_INT,
            sdkVersion = Build.VERSION.RELEASE ?: "",
            securityPatchLevel = securityPatch,
            buildNumber = Build.DISPLAY ?: "",
            isEmulator = isEmulator,
            cpuArchitecture = Build.SUPPORTED_ABIS?.firstOrNull() ?: "",
            processorCount = Runtime.getRuntime().availableProcessors()
        )
    }

    override fun flow(): Flow<SystemInfo> = flowOf(current())
}
