package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricInfo
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [BiometricProvider] for tests. Configure [currentValue] to simulate biometric availability.
 */
class FakeBiometricProvider(initialValue: BiometricInfo = BiometricInfo()) : BiometricProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<BiometricInfo> = _state.asStateFlow()

    var currentValue: BiometricInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): BiometricInfo = _state.value
    override fun flow(): Flow<BiometricInfo> = _state
}
