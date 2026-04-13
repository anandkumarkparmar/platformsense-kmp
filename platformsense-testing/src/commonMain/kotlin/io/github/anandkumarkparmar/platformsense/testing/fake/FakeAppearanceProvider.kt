package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.AppearanceInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AppearanceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [AppearanceProvider] for tests. Configure [currentValue] to simulate appearance state.
 */
class FakeAppearanceProvider(initialValue: AppearanceInfo = AppearanceInfo()) : AppearanceProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<AppearanceInfo> = _state.asStateFlow()

    var currentValue: AppearanceInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): AppearanceInfo = _state.value
    override fun flow(): Flow<AppearanceInfo> = _state
}
