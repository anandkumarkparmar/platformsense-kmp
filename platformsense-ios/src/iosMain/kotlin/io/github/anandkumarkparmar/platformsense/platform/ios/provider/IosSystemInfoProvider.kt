package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.SystemInfo
import io.github.anandkumarkparmar.platformsense.core.provider.SystemInfoProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSProcessInfo
import platform.posix.uname
import platform.posix.utsname

@OptIn(ExperimentalForeignApi::class)
internal class IosSystemInfoProvider : SystemInfoProvider {

    override fun current(): SystemInfo {
        val processInfo = NSProcessInfo.processInfo
        val osVersion = processInfo.operatingSystemVersionString

        val (buildNumber, cpuArch) = memScoped {
            val systemInfo = alloc<utsname>()
            uname(systemInfo.ptr)
            val build = systemInfo.version.toKString()
            val machine = systemInfo.machine.toKString()
            Pair(build, machine)
        }

        // TARGET_OS_SIMULATOR is a compile-time flag; in Kotlin/Native
        // we detect it by checking the architecture
        val isSimulator = cpuArch == "x86_64" ||
            cpuArch == "i386" ||
            cpuArch.contains("simulator", ignoreCase = true)

        return SystemInfo(
            apiLevel = null,
            sdkVersion = osVersion,
            securityPatchLevel = null,
            buildNumber = buildNumber,
            isEmulator = isSimulator,
            cpuArchitecture = cpuArch,
            processorCount = processInfo.processorCount.toInt()
        )
    }

    override fun flow(): Flow<SystemInfo> = flowOf(current())
}
