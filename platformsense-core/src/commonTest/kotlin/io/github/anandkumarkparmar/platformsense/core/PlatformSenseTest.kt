package io.github.anandkumarkparmar.platformsense.core

import io.github.anandkumarkparmar.platformsense.core.models.device.AppInfo
import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricInfo
import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricStatus
import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricType
import io.github.anandkumarkparmar.platformsense.core.models.device.DeviceClass
import io.github.anandkumarkparmar.platformsense.core.models.device.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.models.device.HardwareCapabilities
import io.github.anandkumarkparmar.platformsense.core.models.device.StorageInfo
import io.github.anandkumarkparmar.platformsense.core.models.device.SystemInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.AccessibilityInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.AppearanceInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.DisplayInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.MemoryInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkType
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerState
import io.github.anandkumarkparmar.platformsense.core.models.state.TimezoneInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AccessibilityProvider
import io.github.anandkumarkparmar.platformsense.core.provider.AppInfoProvider
import io.github.anandkumarkparmar.platformsense.core.provider.AppearanceProvider
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
import io.github.anandkumarkparmar.platformsense.core.provider.DisplayProvider
import io.github.anandkumarkparmar.platformsense.core.provider.HardwareCapabilitiesProvider
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
import io.github.anandkumarkparmar.platformsense.core.provider.MemoryProvider
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
import io.github.anandkumarkparmar.platformsense.core.provider.StorageProvider
import io.github.anandkumarkparmar.platformsense.core.provider.SystemInfoProvider
import io.github.anandkumarkparmar.platformsense.core.provider.TimezoneProvider
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class PlatformSenseTest {

    @AfterTest
    fun tearDown() {
        PlatformSense.resetForTest()
    }

    @Test
    fun isInitialized_returnsFalse_beforeInitialize() {
        assertFalse(PlatformSense.isInitialized())
    }

    @Test
    fun isInitialized_returnsTrue_afterInitialize() {
        PlatformSense.initialize(buildWiring())
        assertTrue(PlatformSense.isInitialized())
    }

    @Test
    fun isInitialized_returnsFalse_afterReset() {
        PlatformSense.initialize(buildWiring())
        PlatformSense.resetForTest()
        assertFalse(PlatformSense.isInitialized())
    }

    @Test
    fun network_throwsIllegalStateException_whenNotInitialized() {
        assertFailsWith<IllegalStateException> { PlatformSense.network }
    }

    @Test
    fun biometric_throwsIllegalStateException_whenNotInitialized() {
        assertFailsWith<IllegalStateException> { PlatformSense.biometric }
    }

    @Test
    fun network_returnsCurrent_whenInitialized() {
        PlatformSense.initialize(buildWiring())
        val info = PlatformSense.network.current()
        assertEquals(NetworkType.WIFI, info.type)
        assertTrue(info.isConnected)
    }

    @Test
    fun power_returnsCurrent_whenInitialized() {
        PlatformSense.initialize(buildWiring())
        val info = PlatformSense.power.current()
        assertEquals(PowerState.LOW_POWER, info.status)
        assertEquals(0.15f, info.batteryLevel)
    }

    @Test
    fun device_returnsCurrent_whenInitialized() {
        PlatformSense.initialize(buildWiring())
        val info = PlatformSense.device.current()
        assertEquals(DeviceClass.TABLET, info.deviceClass)
    }

    @Test
    fun biometric_returnsCurrent_whenInitialized() {
        PlatformSense.initialize(buildWiring())
        val info = PlatformSense.biometric.current()
        assertEquals(BiometricStatus.READY, info.status)
        assertEquals(BiometricType.FINGERPRINT, info.type)
    }

    @Test
    fun resetForTest_allowsReinitialization() {
        PlatformSense.initialize(buildWiring())
        PlatformSense.resetForTest()
        PlatformSense.initialize(buildWiring())
        assertTrue(PlatformSense.isInitialized())
    }

    @Test
    fun providers_areCached_acrossAccesses() {
        PlatformSense.initialize(buildWiring())
        val first = PlatformSense.network
        val second = PlatformSense.network
        assertTrue(first === second)
    }

    // --- Helpers ---

    private fun buildWiring(): PlatformSenseWiring = object : PlatformSenseWiring {
        override fun networkProvider(): NetworkProvider = object : NetworkProvider {
            override fun current() =
                NetworkInfo(type = NetworkType.WIFI, isConnected = true, isMetered = false, isRoaming = false)
            override fun flow(): Flow<NetworkInfo> = flowOf(current())
        }

        override fun powerProvider(): PowerProvider = object : PowerProvider {
            override fun current() = PowerInfo(status = PowerState.LOW_POWER, batteryLevel = 0.15f)
            override fun flow(): Flow<PowerInfo> = flowOf(current())
        }

        override fun localeProvider(): LocaleProvider = object : LocaleProvider {
            override fun current() = LocaleInfo(languageTag = "en-US", language = "en", country = "US")
            override fun flow(): Flow<LocaleInfo> = flowOf(current())
        }

        override fun timezoneProvider(): TimezoneProvider = object : TimezoneProvider {
            override fun current() =
                TimezoneInfo(id = "America/New_York", displayName = "EST", offsetMillis = -18_000_000)
            override fun flow(): Flow<TimezoneInfo> = flowOf(current())
        }

        override fun appearanceProvider(): AppearanceProvider = object : AppearanceProvider {
            override fun current() = AppearanceInfo(isDarkMode = false, isDynamicColorAvailable = false)
            override fun flow(): Flow<AppearanceInfo> = flowOf(current())
        }

        override fun displayProvider(): DisplayProvider = object : DisplayProvider {
            override fun current() = DisplayInfo(widthDp = 360, heightDp = 640, density = 2.0f)
            override fun flow(): Flow<DisplayInfo> = flowOf(current())
        }

        override fun deviceProvider(): DeviceProvider = object : DeviceProvider {
            override fun current() = DeviceInfo(deviceClass = DeviceClass.TABLET, osName = "Test")
            override fun flow(): Flow<DeviceInfo> = flowOf(current())
        }

        override fun accessibilityProvider(): AccessibilityProvider = object : AccessibilityProvider {
            override fun current() = AccessibilityInfo()
            override fun flow(): Flow<AccessibilityInfo> = flowOf(current())
        }

        override fun memoryProvider(): MemoryProvider = object : MemoryProvider {
            override fun current() = MemoryInfo(totalRamBytes = 4_000_000_000L)
            override fun flow(): Flow<MemoryInfo> = flowOf(current())
        }

        override fun biometricProvider(): BiometricProvider = object : BiometricProvider {
            override fun current() = BiometricInfo(status = BiometricStatus.READY, type = BiometricType.FINGERPRINT)
            override fun flow(): Flow<BiometricInfo> = flowOf(current())
        }

        override fun hardwareCapabilitiesProvider(): HardwareCapabilitiesProvider =
            object : HardwareCapabilitiesProvider {
                override fun current() = HardwareCapabilities(hasCamera = true, hasGps = true)
                override fun flow(): Flow<HardwareCapabilities> = flowOf(current())
            }

        override fun storageProvider(): StorageProvider = object : StorageProvider {
            override fun current() = StorageInfo(totalBytes = 64_000_000_000L, availableBytes = 32_000_000_000L)
            override fun flow(): Flow<StorageInfo> = flowOf(current())
        }

        override fun systemInfoProvider(): SystemInfoProvider = object : SystemInfoProvider {
            override fun current() = SystemInfo(cpuArchitecture = "test", processorCount = 4)
            override fun flow(): Flow<SystemInfo> = flowOf(current())
        }

        override fun appInfoProvider(): AppInfoProvider = object : AppInfoProvider {
            override fun current() = AppInfo(appName = "TestApp", versionName = "1.0.0")
            override fun flow(): Flow<AppInfo> = flowOf(current())
        }
    }
}
