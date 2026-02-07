package io.platformsense.testing.fake

import io.platformsense.core.provider.BiometricProvider
import io.platformsense.domain.BiometricCapability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [BiometricProvider] for tests. Configure [currentValue] to simulate biometric availability.
 */
class FakeBiometricProvider(
    initialValue: BiometricCapability = BiometricCapability(isAvailable = false),
) : BiometricProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<BiometricCapability> = _state.asStateFlow()

    var currentValue: BiometricCapability
        get() = _state.value
        set(value) { _state.value = value }

    override fun current(): BiometricCapability = _state.value
    override fun flow(): Flow<BiometricCapability> = _state
}
