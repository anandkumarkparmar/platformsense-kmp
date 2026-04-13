package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import io.github.anandkumarkparmar.platformsense.core.models.state.AccessibilityInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AccessibilityProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidAccessibilityProvider(private val context: Context) : AccessibilityProvider {

    override fun current(): AccessibilityInfo = getAccessibilityInfo()

    override fun flow(): Flow<AccessibilityInfo> = callbackFlow {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager

        val listener = AccessibilityManager.AccessibilityStateChangeListener {
            trySend(getAccessibilityInfo())
        }
        am?.addAccessibilityStateChangeListener(listener)
        trySend(current())

        awaitClose {
            am?.removeAccessibilityStateChangeListener(listener)
        }
    }.distinctUntilChanged()

    private fun getAccessibilityInfo(): AccessibilityInfo {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        val config = context.resources.configuration

        val isScreenReader = am?.isTouchExplorationEnabled ?: false
        val fontScale = config.fontScale

        val animatorDurationScale = try {
            Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f)
        } catch (_: Exception) {
            1.0f
        }
        val isReduceMotion = animatorDurationScale == 0.0f

        val isColorInversion = try {
            Settings.Secure.getInt(
                context.contentResolver,
                "accessibility_display_inversion_enabled",
                0
            ) == 1
        } catch (_: Exception) {
            false
        }

        val isHighContrast = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            am?.isRequestFromAccessibilityTool ?: false
        } else {
            false
        }

        return AccessibilityInfo(
            isScreenReaderEnabled = isScreenReader,
            isBoldTextEnabled = false, // Not an Android concept
            fontScale = fontScale,
            isReduceMotionEnabled = isReduceMotion,
            isHighContrastEnabled = isHighContrast,
            isColorInversionEnabled = isColorInversion
        )
    }
}
