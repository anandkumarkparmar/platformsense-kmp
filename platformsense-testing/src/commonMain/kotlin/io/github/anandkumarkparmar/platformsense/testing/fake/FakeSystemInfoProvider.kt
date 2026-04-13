package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.device.SystemInfo
import io.github.anandkumarkparmar.platformsense.core.provider.SystemInfoProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [SystemInfoProvider] for tests. Configure [currentValue] to simulate system info.
 */
public class FakeSystemInfoProvider(initialValue: SystemInfo = SystemInfo()) : SystemInfoProvider {

    private val _state = MutableStateFlow(initialValue)
    public val state: StateFlow<SystemInfo> = _state.asStateFlow()

    public var currentValue: SystemInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): SystemInfo = _state.value
    override fun flow(): Flow<SystemInfo> = _state
}
