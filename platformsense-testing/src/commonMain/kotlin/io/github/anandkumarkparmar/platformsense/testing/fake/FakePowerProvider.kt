package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.provider.PowerProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [PowerProvider] for tests. Configure [currentValue] to simulate power state (e.g. low battery).
 */
class FakePowerProvider(initialValue: PowerInfo = PowerInfo()) : PowerProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<PowerInfo> = _state.asStateFlow()

    var currentValue: PowerInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): PowerInfo = _state.value
    override fun flow(): Flow<PowerInfo> = _state
}
