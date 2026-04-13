package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.device.AppInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AppInfoProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [AppInfoProvider] for tests. Configure [currentValue] to simulate app info.
 */
public class FakeAppInfoProvider(initialValue: AppInfo = AppInfo()) : AppInfoProvider {

    private val _state = MutableStateFlow(initialValue)
    public val state: StateFlow<AppInfo> = _state.asStateFlow()

    public var currentValue: AppInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): AppInfo = _state.value
    override fun flow(): Flow<AppInfo> = _state
}
