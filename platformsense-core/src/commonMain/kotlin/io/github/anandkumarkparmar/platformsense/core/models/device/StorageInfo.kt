package io.github.anandkumarkparmar.platformsense.core.models.device

/**
 * Represents the device storage (disk) information.
 *
 * Use for adaptive behavior such as warning users about low storage
 * or deciding whether to cache large assets.
 *
 * @property totalBytes Total internal storage in bytes.
 * @property availableBytes Available (free) internal storage in bytes.
 */
data class StorageInfo(val totalBytes: Long = 0, val availableBytes: Long = 0)
