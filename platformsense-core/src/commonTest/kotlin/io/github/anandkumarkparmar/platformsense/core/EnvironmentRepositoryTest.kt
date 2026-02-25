package io.github.anandkumarkparmar.platformsense.core

import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceClass
import io.github.anandkumarkparmar.platformsense.core.models.environment.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkType
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerState
import io.github.anandkumarkparmar.platformsense.core.models.environment.TimezoneInfo
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
import io.github.anandkumarkparmar.platformsense.core.provider.TimezoneProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class EnvironmentRepositoryTest {

    @Test
    fun current_returnsSnapshotFromAllProviders() {
        val repo = buildRepository()
        val snapshot = repo.current()
        assertEquals(NetworkType.CELLULAR, snapshot.networkInfo.type)
        assertEquals(PowerState.NORMAL, snapshot.powerInfo.status)
        assertEquals(DeviceClass.PHONE, snapshot.deviceInfo.deviceClass)
        assertEquals("fr-FR", snapshot.localeInfo.languageTag)
        assertEquals("Europe/Paris", snapshot.timezoneInfo.id)
    }

    @Test
    fun current_updatesCachedSnapshot() {
        val repo = buildRepository()
        assertNull(repo.lastCached())
        repo.current()
        assertNotNull(repo.lastCached())
    }

    @Test
    fun lastCached_returnsNull_beforeFirstQuery() {
        val repo = buildRepository()
        assertNull(repo.lastCached())
    }

    @Test
    fun flow_emitsSnapshotCombiningAllProviders() = runTest {
        val repo = buildRepository()
        val snapshot = repo.flow().first()
        assertEquals(NetworkType.CELLULAR, snapshot.networkInfo.type)
        assertEquals(DeviceClass.PHONE, snapshot.deviceInfo.deviceClass)
    }

    @Test
    fun flow_updatesCachedSnapshot_onEmission() = runTest {
        val repo = buildRepository()
        assertNull(repo.lastCached())
        repo.flow().first()
        assertNotNull(repo.lastCached())
    }

    @Test
    fun flow_emitsNewSnapshot_whenProviderEmitsNewValue() = runTest {
        val networkFlow = MutableStateFlow(NetworkInfo(type = NetworkType.WIFI, isConnected = true))
        val repo = EnvironmentRepository(
            networkProvider = {
                object : NetworkProvider {
                    override fun current() = networkFlow.value
                    override fun flow(): Flow<NetworkInfo> = networkFlow
                }
            },
            powerProvider = { staticPowerProvider() },
            deviceProvider = { staticDeviceProvider() },
            localeProvider = { staticLocaleProvider() },
            timezoneProvider = { staticTimezoneProvider() },
        )

        val first = repo.flow().first()
        assertEquals(NetworkType.WIFI, first.networkInfo.type)

        networkFlow.value = NetworkInfo(type = NetworkType.CELLULAR, isConnected = true, isMetered = true)
        val second = repo.flow().first()
        // After the state flow is updated, the next collection should reflect the new value
        assertEquals(NetworkType.CELLULAR, second.networkInfo.type)
    }

    // --- Helpers ---

    private fun buildRepository(): EnvironmentRepository = EnvironmentRepository(
        networkProvider = {
            object : NetworkProvider {
                override fun current() =
                    NetworkInfo(type = NetworkType.CELLULAR, isConnected = true, isMetered = true, isRoaming = false)
                override fun flow(): Flow<NetworkInfo> = flowOf(current())
            }
        },
        powerProvider = { staticPowerProvider() },
        deviceProvider = { staticDeviceProvider() },
        localeProvider = { staticLocaleProvider() },
        timezoneProvider = { staticTimezoneProvider() },
    )

    private fun staticPowerProvider() = object : PowerProvider {
        override fun current() = PowerInfo(status = PowerState.NORMAL, batteryLevel = 0.8f)
        override fun flow(): Flow<PowerInfo> = flowOf(current())
    }

    private fun staticDeviceProvider() = object : DeviceProvider {
        override fun current() = DeviceInfo(deviceClass = DeviceClass.PHONE, osName = "TestOS")
        override fun flow(): Flow<DeviceInfo> = flowOf(current())
    }

    private fun staticLocaleProvider() = object : LocaleProvider {
        override fun current() = LocaleInfo(languageTag = "fr-FR", language = "fr", country = "FR")
        override fun flow(): Flow<LocaleInfo> = flowOf(current())
    }

    private fun staticTimezoneProvider() = object : TimezoneProvider {
        override fun current() = TimezoneInfo(id = "Europe/Paris", displayName = "CET", offsetMillis = 3_600_000)
        override fun flow(): Flow<TimezoneInfo> = flowOf(current())
    }
}
