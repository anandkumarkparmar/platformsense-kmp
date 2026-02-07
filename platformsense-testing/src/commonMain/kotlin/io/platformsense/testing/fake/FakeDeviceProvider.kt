package io.platformsense.testing.fake

import io.platformsense.core.provider.DeviceProvider
import io.platformsense.domain.DeviceClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [DeviceProvider] for tests. Configure [currentValue] to simulate device class (phone, tablet, etc.).
 */
class FakeDeviceProvider(
    initialValue: DeviceClass = DeviceClass.UNKNOWN,
) : DeviceProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<DeviceClass> = _state.asStateFlow()

    var currentValue: DeviceClass
        get() = _state.value
        set(value) { _state.value = value }

    override fun current(): DeviceClass = _state.value
    override fun flow(): Flow<DeviceClass> = _state
}
