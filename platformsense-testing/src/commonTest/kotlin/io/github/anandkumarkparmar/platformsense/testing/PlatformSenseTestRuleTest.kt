package io.github.anandkumarkparmar.platformsense.testing

import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.NetworkType
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.PowerState
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlatformSenseTestRuleTest {

    private val rule = PlatformSenseTestRule()

    @AfterTest
    fun tearDown() {
        rule.uninstall()
    }

    @Test
    fun install_initializesPlatformSense() {
        assertFalse(PlatformSense.isInitialized())
        rule.install()
        assertTrue(PlatformSense.isInitialized())
    }

    @Test
    fun uninstall_clearsPlatformSense() {
        rule.install()
        rule.uninstall()
        assertFalse(PlatformSense.isInitialized())
    }

    @Test
    fun wiring_fakePower_reflectsDirectly() {
        rule.install()
        rule.wiring.power.currentValue = PowerInfo(status = PowerState.LOW_POWER, batteryLevel = 0.1f)
        val info = PlatformSense.power.current()
        assertEquals(PowerState.LOW_POWER, info.status)
        assertEquals(0.1f, info.batteryLevel)
    }

    @Test
    fun wiring_fakeNetwork_reflectsDirectly() {
        rule.install()
        rule.wiring.network.currentValue =
            NetworkInfo(type = NetworkType.CELLULAR, isConnected = true, isMetered = true)
        val info = PlatformSense.network.current()
        assertEquals(NetworkType.CELLULAR, info.type)
        assertTrue(info.isMetered)
    }
}
