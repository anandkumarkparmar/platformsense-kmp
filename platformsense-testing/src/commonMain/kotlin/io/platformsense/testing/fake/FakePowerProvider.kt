package io.platformsense.testing.fake

import io.platformsense.core.provider.PowerProvider
import io.platformsense.domain.PowerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [PowerProvider] for tests. Configure [currentValue] to simulate power state (e.g. low battery).
 */
class FakePowerProvider(initialValue: PowerState = PowerState.UNKNOWN) : PowerProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<PowerState> = _state.asStateFlow()

    var currentValue: PowerState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): PowerState = _state.value
    override fun flow(): Flow<PowerState> = _state
}
