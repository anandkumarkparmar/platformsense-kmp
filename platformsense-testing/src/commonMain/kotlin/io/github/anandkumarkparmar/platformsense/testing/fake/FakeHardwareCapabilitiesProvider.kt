package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.device.HardwareCapabilities
import io.github.anandkumarkparmar.platformsense.core.provider.HardwareCapabilitiesProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [HardwareCapabilitiesProvider] for tests. Configure [currentValue] to simulate hardware.
 */
class FakeHardwareCapabilitiesProvider(initialValue: HardwareCapabilities = HardwareCapabilities()) :
    HardwareCapabilitiesProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<HardwareCapabilities> = _state.asStateFlow()

    var currentValue: HardwareCapabilities
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): HardwareCapabilities = _state.value
    override fun flow(): Flow<HardwareCapabilities> = _state
}
