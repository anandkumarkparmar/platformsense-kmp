package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.TimezoneInfo
import io.github.anandkumarkparmar.platformsense.core.provider.TimezoneProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [TimezoneProvider] for tests. Configure [currentValue] to simulate timezone info.
 */
public class FakeTimezoneProvider(initialValue: TimezoneInfo = TimezoneInfo()) : TimezoneProvider {

    private val _state = MutableStateFlow(initialValue)
    public val state: StateFlow<TimezoneInfo> = _state.asStateFlow()

    public var currentValue: TimezoneInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): TimezoneInfo = _state.value
    override fun flow(): Flow<TimezoneInfo> = _state
}
