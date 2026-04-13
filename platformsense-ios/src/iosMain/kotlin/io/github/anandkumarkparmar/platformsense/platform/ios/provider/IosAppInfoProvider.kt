package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.AppInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AppInfoProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSBundle

class IosAppInfoProvider : AppInfoProvider {

    override fun current(): AppInfo {
        val bundle = NSBundle.mainBundle
        val info = bundle.infoDictionary ?: emptyMap<Any?, Any?>()
        return AppInfo(
            appName = (info["CFBundleDisplayName"] as? String)
                ?: (info["CFBundleName"] as? String) ?: "",
            packageName = bundle.bundleIdentifier ?: "",
            versionName = (info["CFBundleShortVersionString"] as? String) ?: "",
            versionCode = (info["CFBundleVersion"] as? String)?.toLongOrNull() ?: 0,
            firstInstallTime = null,
            lastUpdateTime = null
        )
    }

    override fun flow(): Flow<AppInfo> = flowOf(current())
}
