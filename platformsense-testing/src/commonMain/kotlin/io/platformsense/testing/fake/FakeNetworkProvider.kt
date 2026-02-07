package io.platformsense.testing.fake

import io.platformsense.core.provider.NetworkProvider
import io.platformsense.domain.NetworkType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [NetworkProvider] for tests. Configure [currentValue] to simulate network state.
 */
class FakeNetworkProvider(
    initialValue: NetworkType = NetworkType.UNKNOWN,
) : NetworkProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<NetworkType> = _state.asStateFlow()

    var currentValue: NetworkType
        get() = _state.value
        set(value) { _state.value = value }

    override fun current(): NetworkType = _state.value
    override fun flow(): Flow<NetworkType> = _state
}
