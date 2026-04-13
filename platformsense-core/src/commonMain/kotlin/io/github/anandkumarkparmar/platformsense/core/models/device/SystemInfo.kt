package io.github.anandkumarkparmar.platformsense.core.models.device

/**
 * Represents extended system and hardware information.
 *
 * Use for diagnostics, analytics, and adaptive behavior based on hardware capabilities.
 *
 * @property apiLevel Android API level (e.g. 34). Null on iOS.
 * @property sdkVersion OS SDK version string (e.g. "14" on Android, "17.4" on iOS).
 * @property securityPatchLevel Android security patch level (e.g. "2024-01-05"). Null on iOS or pre-API 23.
 * @property buildNumber OS build number/identifier.
 * @property isEmulator True if the app is running on an emulator or simulator.
 * @property cpuArchitecture Primary CPU architecture (e.g. "arm64-v8a", "arm64").
 * @property processorCount Number of available processors/cores.
 */
public data class SystemInfo(
    val apiLevel: Int? = null,
    val sdkVersion: String = "",
    val securityPatchLevel: String? = null,
    val buildNumber: String = "",
    val isEmulator: Boolean = false,
    val cpuArchitecture: String = "",
    val processorCount: Int = 1
)
