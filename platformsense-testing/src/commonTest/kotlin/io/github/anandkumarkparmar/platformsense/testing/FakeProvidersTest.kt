package io.github.anandkumarkparmar.platformsense.testing

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
import io.github.anandkumarkparmar.platformsense.testing.fake.FakeBiometricProvider
import io.github.anandkumarkparmar.platformsense.testing.fake.FakeDeviceProvider
import io.github.anandkumarkparmar.platformsense.testing.fake.FakeLocaleProvider
import io.github.anandkumarkparmar.platformsense.testing.fake.FakeNetworkProvider
import io.github.anandkumarkparmar.platformsense.testing.fake.FakePowerProvider
import io.github.anandkumarkparmar.platformsense.testing.fake.FakeTimezoneProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class FakeProvidersTest {

    @Test
    fun fakeNetworkProvider_current_returnsInitialValue() {
        val provider = FakeNetworkProvider(NetworkInfo(type = NetworkType.WIFI, isConnected = true))
        assertEquals(NetworkType.WIFI, provider.current().type)
    }

    @Test
    fun fakeNetworkProvider_current_reflectsUpdatedValue() {
        val provider = FakeNetworkProvider()
        provider.currentValue = NetworkInfo(type = NetworkType.CELLULAR, isConnected = true, isMetered = true)
        assertEquals(NetworkType.CELLULAR, provider.current().type)
        assertEquals(true, provider.current().isMetered)
    }

    @Test
    fun fakeNetworkProvider_flow_emitsNewValue_whenCurrentValueChanges() = runTest {
        val provider = FakeNetworkProvider()
        provider.currentValue = NetworkInfo(type = NetworkType.ETHERNET, isConnected = true)
        val emitted = provider.flow().first()
        assertEquals(NetworkType.ETHERNET, emitted.type)
    }

    @Test
    fun fakePowerProvider_current_reflectsUpdatedValue() {
        val provider = FakePowerProvider()
        provider.currentValue = PowerInfo(status = PowerState.LOW_POWER, batteryLevel = 0.05f, isCharging = false)
        assertEquals(PowerState.LOW_POWER, provider.current().status)
        assertEquals(0.05f, provider.current().batteryLevel)
    }

    @Test
    fun fakePowerProvider_flow_emitsUpdatedValue() = runTest {
        val provider = FakePowerProvider()
        provider.currentValue = PowerInfo(status = PowerState.NORMAL, batteryLevel = 0.9f, isCharging = true)
        val emitted = provider.flow().first()
        assertEquals(PowerState.NORMAL, emitted.status)
        assertEquals(true, emitted.isCharging)
    }

    @Test
    fun fakeDeviceProvider_current_reflectsUpdatedValue() {
        val provider = FakeDeviceProvider()
        provider.currentValue = DeviceInfo(deviceClass = DeviceClass.TABLET, osName = "iOS", manufacturer = "Apple")
        assertEquals(DeviceClass.TABLET, provider.current().deviceClass)
        assertEquals("Apple", provider.current().manufacturer)
    }

    @Test
    fun fakeLocaleProvider_current_reflectsUpdatedValue() {
        val provider = FakeLocaleProvider()
        provider.currentValue =
            LocaleInfo(languageTag = "ja-JP", language = "ja", country = "JP", is24HourFormat = true)
        assertEquals("ja-JP", provider.current().languageTag)
        assertEquals(true, provider.current().is24HourFormat)
    }

    @Test
    fun fakeTimezoneProvider_current_reflectsUpdatedValue() {
        val provider = FakeTimezoneProvider()
        provider.currentValue = TimezoneInfo(id = "Asia/Tokyo", displayName = "JST", offsetMillis = 32_400_000)
        assertEquals("Asia/Tokyo", provider.current().id)
        assertEquals(32_400_000, provider.current().offsetMillis)
    }

    @Test
    fun fakeBiometricProvider_current_reflectsUpdatedValue() {
        val provider = FakeBiometricProvider()
        provider.currentValue = BiometricInfo(status = BiometricStatus.READY, type = BiometricType.FACE)
        assertEquals(BiometricStatus.READY, provider.current().status)
        assertEquals(BiometricType.FACE, provider.current().type)
    }

    @Test
    fun fakeBiometricProvider_flow_emitsUpdatedValue() = runTest {
        val provider = FakeBiometricProvider()
        provider.currentValue = BiometricInfo(status = BiometricStatus.NOT_ENROLLED, type = BiometricType.FINGERPRINT)
        val emitted = provider.flow().first()
        assertEquals(BiometricStatus.NOT_ENROLLED, emitted.status)
    }
}
