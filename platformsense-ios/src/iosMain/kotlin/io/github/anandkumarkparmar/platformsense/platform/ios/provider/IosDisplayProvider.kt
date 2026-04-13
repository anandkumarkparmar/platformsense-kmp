package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.DisplayInfo
import io.github.anandkumarkparmar.platformsense.core.models.state.ScreenOrientation
import io.github.anandkumarkparmar.platformsense.core.provider.DisplayProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation
import platform.UIKit.UIDeviceOrientationDidChangeNotification
import platform.UIKit.UIScreen

@OptIn(ExperimentalForeignApi::class)
internal class IosDisplayProvider : DisplayProvider {

    override fun current(): DisplayInfo = getDisplayInfo()

    override fun flow(): Flow<DisplayInfo> = callbackFlow {
        val center = NSNotificationCenter.defaultCenter
        val queue = NSOperationQueue.mainQueue

        UIDevice.currentDevice.beginGeneratingDeviceOrientationNotifications()

        val observer = center.addObserverForName(
            name = UIDeviceOrientationDidChangeNotification,
            `object` = null,
            queue = queue,
        ) { _ ->
            trySend(getDisplayInfo())
        }

        trySend(current())

        awaitClose {
            center.removeObserver(observer)
            UIDevice.currentDevice.endGeneratingDeviceOrientationNotifications()
        }
    }.distinctUntilChanged()

    private fun getDisplayInfo(): DisplayInfo {
        val screen = UIScreen.mainScreen
        val scale = screen.scale.toFloat()

        val (widthPoints, heightPoints) = screen.bounds.useContents {
            Pair(size.width.toInt(), size.height.toInt())
        }

        val widthPixels = (widthPoints * scale).toInt()
        val heightPixels = (heightPoints * scale).toInt()
        val refreshRate = screen.maximumFramesPerSecond.toFloat()

        val deviceOrientation = UIDevice.currentDevice.orientation
        val orientation = when (deviceOrientation) {
            UIDeviceOrientation.UIDeviceOrientationPortrait,
            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown,
            -> ScreenOrientation.PORTRAIT
            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft,
            UIDeviceOrientation.UIDeviceOrientationLandscapeRight,
            -> ScreenOrientation.LANDSCAPE
            else -> if (widthPoints < heightPoints) {
                ScreenOrientation.PORTRAIT
            } else {
                ScreenOrientation.LANDSCAPE
            }
        }

        val hasCutout = heightPoints > 800 && scale >= 3.0f

        return DisplayInfo(
            widthDp = widthPoints,
            heightDp = heightPoints,
            widthPixels = widthPixels,
            heightPixels = heightPixels,
            density = scale,
            refreshRate = refreshRate,
            orientation = orientation,
            hasCutout = hasCutout,
        )
    }
}
