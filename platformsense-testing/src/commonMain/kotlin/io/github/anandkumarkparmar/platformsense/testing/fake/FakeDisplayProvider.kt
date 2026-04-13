package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.DisplayInfo
import io.github.anandkumarkparmar.platformsense.core.provider.DisplayProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [DisplayProvider] for tests. Configure [currentValue] to simulate display state.
 */
class FakeDisplayProvider(initialValue: DisplayInfo = DisplayInfo()) : DisplayProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<DisplayInfo> = _state.asStateFlow()

    var currentValue: DisplayInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): DisplayInfo = _state.value
    override fun flow(): Flow<DisplayInfo> = _state
}
