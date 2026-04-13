package io.github.anandkumarkparmar.platformsense.testing.fake

import io.github.anandkumarkparmar.platformsense.core.models.state.AccessibilityInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AccessibilityProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Fake [AccessibilityProvider] for tests. Configure [currentValue] to simulate accessibility state.
 */
class FakeAccessibilityProvider(initialValue: AccessibilityInfo = AccessibilityInfo()) : AccessibilityProvider {

    private val _state = MutableStateFlow(initialValue)
    val state: StateFlow<AccessibilityInfo> = _state.asStateFlow()

    var currentValue: AccessibilityInfo
        get() = _state.value
        set(value) {
            _state.value = value
        }

    override fun current(): AccessibilityInfo = _state.value
    override fun flow(): Flow<AccessibilityInfo> = _state
}
