package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.device.DeviceInfo
import io.github.anandkumarkparmar.platformsense.core.provider.DeviceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [DeviceProvider] for tests. Configure [currentValue] to simulate device class (phone, tablet, etc.).
 */
class FakeDeviceProvider(initialValue: DeviceInfo = DeviceInfo()) : DeviceProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<DeviceInfo> = _state.asStateFlow()

    var currentValue: DeviceInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): DeviceInfo = _state.value
    override fun flow(): Flow<DeviceInfo> = _state
}
