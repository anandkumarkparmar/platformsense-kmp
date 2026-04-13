package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.state.AppearanceInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AppearanceProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

class IosAppearanceProvider : AppearanceProvider {

    override fun current(): AppearanceInfo = getAppearanceInfo()

    override fun flow(): Flow<AppearanceInfo> = callbackFlow {
        trySend(current())
        awaitClose {}
    }.distinctUntilChanged()

    private fun getAppearanceInfo(): AppearanceInfo {
        val style = UIScreen.mainScreen.traitCollection.userInterfaceStyle
        return AppearanceInfo(
            isDarkMode = style == UIUserInterfaceStyle.UIUserInterfaceStyleDark,
            isDynamicColorAvailable = false,
        )
    }
}
