package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.MemoryInfo
import io.github.anandkumarkparmar.platformsense.core.provider.MemoryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [MemoryProvider] for tests. Configure [currentValue] to simulate memory state.
 */
public class FakeMemoryProvider(initialValue: MemoryInfo = MemoryInfo()) : MemoryProvider {

    private val _state = MutableStateFlow(initialValue)
    public val state: StateFlow<MemoryInfo> = _state.asStateFlow()

    public var currentValue: MemoryInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): MemoryInfo = _state.value
    override fun flow(): Flow<MemoryInfo> = _state
}
