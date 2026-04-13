package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.AccessibilityInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AccessibilityProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAccessibilityBoldTextStatusDidChangeNotification
import platform.UIKit.UIAccessibilityInvertColorsStatusDidChangeNotification
import platform.UIKit.UIAccessibilityIsBoldTextEnabled
import platform.UIKit.UIAccessibilityIsInvertColorsEnabled
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled
import platform.UIKit.UIAccessibilityIsVoiceOverRunning
import platform.UIKit.UIAccessibilityReduceMotionStatusDidChangeNotification
import platform.UIKit.UIAccessibilityVoiceOverStatusDidChangeNotification
import platform.UIKit.UIApplication
import platform.UIKit.UIContentSizeCategoryAccessibilityExtraExtraExtraLarge
import platform.UIKit.UIContentSizeCategoryAccessibilityExtraExtraLarge
import platform.UIKit.UIContentSizeCategoryAccessibilityExtraLarge
import platform.UIKit.UIContentSizeCategoryAccessibilityLarge
import platform.UIKit.UIContentSizeCategoryAccessibilityMedium
import platform.UIKit.UIContentSizeCategoryExtraExtraExtraLarge
import platform.UIKit.UIContentSizeCategoryExtraExtraLarge
import platform.UIKit.UIContentSizeCategoryExtraLarge
import platform.UIKit.UIContentSizeCategoryExtraSmall
import platform.UIKit.UIContentSizeCategoryLarge
import platform.UIKit.UIContentSizeCategoryMedium
import platform.UIKit.UIContentSizeCategorySmall

internal class IosAccessibilityProvider : AccessibilityProvider {

    override fun current(): AccessibilityInfo = getAccessibilityInfo()

    override fun flow(): Flow<AccessibilityInfo> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        val notifications = listOf(
            UIAccessibilityVoiceOverStatusDidChangeNotification,
            UIAccessibilityBoldTextStatusDidChangeNotification,
            UIAccessibilityReduceMotionStatusDidChangeNotification,
            UIAccessibilityInvertColorsStatusDidChangeNotification,
        )

        val observers = notifications.map { name ->
            center.addObserverForName(
                name = name,
                `object` = null,
                queue = queue,
            ) { _ ->
                trySend(getAccessibilityInfo())
            }
        }

        trySend(current())

        awaitClose {
            observers.forEach { center.removeObserver(it) }
        }
    }.distinctUntilChanged()

    private fun getAccessibilityInfo(): AccessibilityInfo {
        val category = UIApplication.sharedApplication.preferredContentSizeCategory
        val fontScale = mapContentSizeCategoryToScale(category)

        return AccessibilityInfo(
            isScreenReaderEnabled = UIAccessibilityIsVoiceOverRunning(),
            isBoldTextEnabled = UIAccessibilityIsBoldTextEnabled(),
            fontScale = fontScale,
            isReduceMotionEnabled = UIAccessibilityIsReduceMotionEnabled(),
            isHighContrastEnabled = false,
            isColorInversionEnabled = UIAccessibilityIsInvertColorsEnabled(),
        )
    }

    private fun mapContentSizeCategoryToScale(category: String?): Float = when (category) {
        UIContentSizeCategoryExtraSmall -> 0.82f
        UIContentSizeCategorySmall -> 0.88f
        UIContentSizeCategoryMedium -> 0.94f
        UIContentSizeCategoryLarge -> 1.0f
        UIContentSizeCategoryExtraLarge -> 1.12f
        UIContentSizeCategoryExtraExtraLarge -> 1.24f
        UIContentSizeCategoryExtraExtraExtraLarge -> 1.35f
        UIContentSizeCategoryAccessibilityMedium -> 1.6f
        UIContentSizeCategoryAccessibilityLarge -> 1.9f
        UIContentSizeCategoryAccessibilityExtraLarge -> 2.35f
        UIContentSizeCategoryAccessibilityExtraExtraLarge -> 2.75f
        UIContentSizeCategoryAccessibilityExtraExtraExtraLarge -> 3.1f
        else -> 1.0f
    }
}
