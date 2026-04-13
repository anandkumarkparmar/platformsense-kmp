package io.github.anandkumarkparmar.platformsense.core.models.state

/**
 * Represents the device memory (RAM) information.
 *
 * Use for adaptive behavior such as reducing in-memory caches when memory is low
 * or adjusting image resolution based on available RAM.
 *
 * @property totalRamBytes Total physical RAM in bytes.
 * @property availableRamBytes Available RAM in bytes, if determinable. Null if unknown.
 * @property isLowMemory True if the system is currently under memory pressure.
 */
public data class MemoryInfo(
    val totalRamBytes: Long = 0,
    val availableRamBytes: Long? = null,
    val isLowMemory: Boolean = false
)
