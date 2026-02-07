package io.platformsense.testing

import io.platformsense.core.PlatformSense
import io.platformsense.testing.fake.FakePlatformSenseWiring

/**
 * Holds fake providers and wiring for overriding [PlatformSense] in tests.
 *
 * Usage:
 * 1. Create a [FakePlatformSenseWiring] (or use [wiring]).
 * 2. Configure fakes (e.g. [FakePlatformSenseWiring.power].currentValue = PowerState.LOW_POWER).
 * 3. Call [install] to initialize PlatformSense with the fakes.
 * 4. Run test code that uses PlatformSense.environment(), environmentFlow, capabilities().
 * 5. Call [uninstall] in teardown (e.g. @After) to clear PlatformSense for the next test.
 *
 * Example (JUnit):
 * ```
 * val rule = PlatformSenseTestRule()
 * @Before fun setup() { rule.install() }
 * @After fun teardown() { rule.uninstall() }
 * @Test fun lowPower() {
 *   rule.wiring.power.currentValue = PowerState.LOW_POWER
 *   assertTrue(PlatformSense.environment().powerState == PowerState.LOW_POWER)
 * }
 * ```
 */
class PlatformSenseTestRule {

    val wiring: FakePlatformSenseWiring = FakePlatformSenseWiring()

    /**
     * Initializes PlatformSense with the fake wiring. Call in test setup.
     */
    fun install() {
        PlatformSense.initialize(wiring)
    }

    /**
     * Clears PlatformSense so the next test can re-initialize. Call in test teardown.
     */
    fun uninstall() {
        PlatformSense.resetForTest()
    }
}
