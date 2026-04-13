package io.github.anandkumarkparmar.platformsense.core.models.device

/**
 * Represents the app identity and metadata.
 *
 * Use for analytics, about screens, version checks, and update prompts.
 *
 * @property appName The user-visible app name (e.g. "My App").
 * @property packageName The package/bundle identifier (e.g. "com.example.myapp").
 * @property versionName The user-visible version string (e.g. "1.2.3").
 * @property versionCode The internal build number used for update ordering.
 * @property firstInstallTime Epoch millis of first install, if available. Null on iOS.
 * @property lastUpdateTime Epoch millis of last update, if available. Null on iOS.
 */
data class AppInfo(
    val appName: String = "",
    val packageName: String = "",
    val versionName: String = "",
    val versionCode: Long = 0,
    val firstInstallTime: Long? = null,
    val lastUpdateTime: Long? = null
)
