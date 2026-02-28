package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.environment.LocaleInfo
import io.github.anandkumarkparmar.platformsense.core.provider.LocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [LocaleProvider] for tests. Configure [currentValue] to simulate locale info.
 */
class FakeLocaleProvider(initialValue: LocaleInfo = LocaleInfo()) : LocaleProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<LocaleInfo> = _state.asStateFlow()

    var currentValue: LocaleInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): LocaleInfo = _state.value
    override fun flow(): Flow<LocaleInfo> = _state
}
