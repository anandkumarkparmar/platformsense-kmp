package io.github.anandkumarkparmar.platformsense.core.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.AppInfo
import kotlinx.coroutines.flow.Flow

/**
 * Provides app identity and metadata as a snapshot and as a reactive stream.
 *
 * App info is static for the lifetime of the process. Use [current] for one-off
 * queries (e.g. about screen, analytics events).
 */
interface AppInfoProvider {

    fun current(): AppInfo

    fun flow(): Flow<AppInfo>
}
