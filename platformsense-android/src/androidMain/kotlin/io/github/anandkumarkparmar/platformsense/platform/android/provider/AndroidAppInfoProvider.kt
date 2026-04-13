package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.os.Build
import io.github.anandkumarkparmar.platformsense.core.models.device.AppInfo
import io.github.anandkumarkparmar.platformsense.core.provider.AppInfoProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class AndroidAppInfoProvider(private val context: Context) : AppInfoProvider {

    override fun current(): AppInfo {
        val packageName = context.packageName
        val packageInfo = context.packageManager.getPackageInfo(packageName, 0)
        val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
        val versionName = packageInfo.versionName ?: ""
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        return AppInfo(
            appName = appName,
            packageName = packageName,
            versionName = versionName,
            versionCode = versionCode,
            firstInstallTime = packageInfo.firstInstallTime,
            lastUpdateTime = packageInfo.lastUpdateTime
        )
    }

    override fun flow(): Flow<AppInfo> = flowOf(current())
}
