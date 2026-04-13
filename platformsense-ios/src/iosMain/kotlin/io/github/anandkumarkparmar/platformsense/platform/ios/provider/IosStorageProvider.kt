package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.StorageInfo
import io.github.anandkumarkparmar.platformsense.core.provider.StorageProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSystemFreeSize
import platform.Foundation.NSFileSystemSize
import platform.Foundation.NSHomeDirectory

@OptIn(ExperimentalForeignApi::class)
internal class IosStorageProvider : StorageProvider {

    override fun current(): StorageInfo {
        val attrs = try {
            NSFileManager.defaultManager.attributesOfFileSystemForPath(NSHomeDirectory(), null)
        } catch (_: Exception) {
            null
        }
        val totalBytes = (attrs?.get(NSFileSystemSize) as? Number)?.toLong() ?: 0L
        val freeBytes = (attrs?.get(NSFileSystemFreeSize) as? Number)?.toLong() ?: 0L
        return StorageInfo(
            totalBytes = totalBytes,
            availableBytes = freeBytes,
        )
    }

    override fun flow(): Flow<StorageInfo> = flowOf(current())
}
