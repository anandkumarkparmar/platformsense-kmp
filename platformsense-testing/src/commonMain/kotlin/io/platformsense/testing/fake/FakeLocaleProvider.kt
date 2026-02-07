package io.platformsense.testing.fake

import io.platformsense.core.provider.LocaleProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [LocaleProvider] for tests. Configure [currentValue] to simulate locale (e.g. "en-US").
 */
class FakeLocaleProvider(
    initialValue: String = "",
) : LocaleProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<String> = _state.asStateFlow()

    var currentValue: String
        get() = _state.value
        set(value) { _state.value = value }

    override fun current(): String = _state.value
    override fun flow(): Flow<String> = _state
}
