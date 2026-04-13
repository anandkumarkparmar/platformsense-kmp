package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.device.StorageInfo
import io.github.anandkumarkparmar.platformsense.core.provider.StorageProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [StorageProvider] for tests. Configure [currentValue] to simulate storage info.
 */
class FakeStorageProvider(initialValue: StorageInfo = StorageInfo()) : StorageProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<StorageInfo> = _state.asStateFlow()

    var currentValue: StorageInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): StorageInfo = _state.value
    override fun flow(): Flow<StorageInfo> = _state
}
