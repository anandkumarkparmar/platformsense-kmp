package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.provider.NetworkProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [NetworkProvider] for tests. Configure [currentValue] to simulate network state.
 */
class FakeNetworkProvider(initialValue: NetworkInfo = NetworkInfo()) : NetworkProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<NetworkInfo> = _state.asStateFlow()

    var currentValue: NetworkInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): NetworkInfo = _state.value
    override fun flow(): Flow<NetworkInfo> = _state
}
