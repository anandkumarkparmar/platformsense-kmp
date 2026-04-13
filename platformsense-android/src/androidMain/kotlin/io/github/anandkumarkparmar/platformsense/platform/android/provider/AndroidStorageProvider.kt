package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.os.Environment
import android.os.StatFs
import io.github.anandkumarkparmar.platformsense.core.models.device.StorageInfo
import io.github.anandkumarkparmar.platformsense.core.provider.StorageProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class AndroidStorageProvider : StorageProvider {

    override fun current(): StorageInfo {
        val stat = StatFs(Environment.getDataDirectory().path)
        return StorageInfo(
            totalBytes = stat.totalBytes,
            availableBytes = stat.availableBytes
        )
    }

    override fun flow(): Flow<StorageInfo> = flowOf(current())
}
