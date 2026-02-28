package io.github.anandkumarkparmar.platformsense.core

import io.github.anandkumarkparmar.platformsense.core.models.capability.BiometricInfo
import io.github.anandkumarkparmar.platformsense.core.models.capability.BiometricStatus
import io.github.anandkumarkparmar.platformsense.core.models.capability.BiometricType
import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceClass
import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkType
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerState
import io.github.anandkumarkparmar.platformsense.core.models.environment.TimezoneInfo
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
import io.github.anandkumarkparmar.platformsense.core.provider.TimezoneProvider
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
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
        PlatformSense.initialize(buildEnvironmentRepository(), buildCapabilitiesRepository())
        assertTrue(PlatformSense.isInitialized())
    }

    @Test
    fun isInitialized_returnsFalse_afterReset() {
        PlatformSense.initialize(buildEnvironmentRepository(), buildCapabilitiesRepository())
        PlatformSense.resetForTest()
        assertFalse(PlatformSense.isInitialized())
    }

    @Test
    fun environment_throwsIllegalStateException_whenNotInitialized() {
        assertFailsWith<IllegalStateException> { PlatformSense.environment() }
    }

    @Test
    fun capabilities_throwsIllegalStateException_whenNotInitialized() {
        assertFailsWith<IllegalStateException> { PlatformSense.capabilities() }
    }

    @Test
    fun environment_returnsSnapshot_whenInitialized() {
        PlatformSense.initialize(buildEnvironmentRepository(), buildCapabilitiesRepository())
        val env = PlatformSense.environment()
        assertNotNull(env)
        assertEquals(NetworkType.WIFI, env.networkInfo.type)
        assertEquals(PowerState.LOW_POWER, env.powerInfo.status)
        assertEquals(DeviceClass.TABLET, env.deviceInfo.deviceClass)
    }

    @Test
    fun capabilities_returnsSnapshot_whenInitialized() {
        PlatformSense.initialize(buildEnvironmentRepository(), buildCapabilitiesRepository())
        val caps = PlatformSense.capabilities()
        assertNotNull(caps)
        assertEquals(BiometricStatus.READY, caps.biometric.status)
        assertEquals(BiometricType.FINGERPRINT, caps.biometric.type)
    }

    @Test
    fun resetForTest_allowsReinitialization() {
        PlatformSense.initialize(buildEnvironmentRepository(), buildCapabilitiesRepository())
        PlatformSense.resetForTest()
        // Should be able to initialize again without error
        PlatformSense.initialize(buildEnvironmentRepository(), buildCapabilitiesRepository())
        assertTrue(PlatformSense.isInitialized())
    }

    // --- Helpers ---

    private fun buildEnvironmentRepository(): EnvironmentRepository = EnvironmentRepository(
        networkProvider = {
            object : NetworkProvider {
                override fun current() =
                    NetworkInfo(type = NetworkType.WIFI, isConnected = true, isMetered = false, isRoaming = false)
                override fun flow(): Flow<NetworkInfo> = flowOf(current())
            }
        },
        powerProvider = {
            object : PowerProvider {
                override fun current() = PowerInfo(status = PowerState.LOW_POWER, batteryLevel = 0.15f)
                override fun flow(): Flow<PowerInfo> = flowOf(current())
            }
        },
        deviceProvider = {
            object : DeviceProvider {
                override fun current() = DeviceInfo(deviceClass = DeviceClass.TABLET, osName = "Test")
                override fun flow(): Flow<DeviceInfo> = flowOf(current())
            }
        },
        localeProvider = {
            object : LocaleProvider {
                override fun current() = LocaleInfo(languageTag = "en-US", language = "en", country = "US")
                override fun flow(): Flow<LocaleInfo> = flowOf(current())
            }
        },
        timezoneProvider = {
            object : TimezoneProvider {
                override fun current() =
                    TimezoneInfo(id = "America/New_York", displayName = "EST", offsetMillis = -18_000_000)
                override fun flow(): Flow<TimezoneInfo> = flowOf(current())
            }
        },
    )

    private fun buildCapabilitiesRepository(): CapabilitiesRepository = CapabilitiesRepository(
        biometricProvider = {
            object : BiometricProvider {
                override fun current() = BiometricInfo(status = BiometricStatus.READY, type = BiometricType.FINGERPRINT)
                override fun flow(): Flow<BiometricInfo> = flowOf(current())
            }
        },
    )
}
