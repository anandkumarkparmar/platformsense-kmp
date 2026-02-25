package io.github.anandkumarkparmar.platformsense.testing

import io.github.anandkumarkparmar.platformsense.core.PlatformSense
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.NetworkType
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerInfo
import io.github.anandkumarkparmar.platformsense.core.models.environment.PowerState
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
    fun wiring_fakePower_reflectsInEnvironmentSnapshot() {
        rule.install()
        rule.wiring.power.currentValue = PowerInfo(status = PowerState.LOW_POWER, batteryLevel = 0.1f)
        val env = PlatformSense.environment()
        assertEquals(PowerState.LOW_POWER, env.powerInfo.status)
        assertEquals(0.1f, env.powerInfo.batteryLevel)
    }

    @Test
    fun wiring_fakeNetwork_reflectsInEnvironmentSnapshot() {
        rule.install()
        rule.wiring.network.currentValue =
            NetworkInfo(type = NetworkType.CELLULAR, isConnected = true, isMetered = true)
        val env = PlatformSense.environment()
        assertEquals(NetworkType.CELLULAR, env.networkInfo.type)
        assertTrue(env.networkInfo.isMetered)
    }
}
