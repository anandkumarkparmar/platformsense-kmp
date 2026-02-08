package io.platformsense.core

import io.platformsense.core.provider.DeviceProvider
import io.platformsense.core.provider.LocaleProvider
import io.platformsense.core.provider.NetworkProvider
import io.platformsense.core.provider.PowerProvider
import io.platformsense.core.provider.TimezoneProvider
import io.platformsense.domain.EnvironmentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach

/**
 * Aggregates environment providers into a single [EnvironmentSnapshot] and a reactive [Flow].
 *
 * Combines [NetworkProvider], [PowerProvider], [DeviceProvider], [LocaleProvider], and
 * [TimezoneProvider]. Providers are resolved lazily on first use. The last snapshot is
 * cached and updated whenever the environment flow emits.
 *
 * Use [current] for one-off snapshot queries and [flow] for UI that reacts to environment
 * changes (e.g. network, power, locale).
 */
class EnvironmentRepository(
    private val networkProvider: () -> NetworkProvider,
    private val powerProvider: () -> PowerProvider,
    private val deviceProvider: () -> DeviceProvider,
    private val localeProvider: () -> LocaleProvider,
    private val timezoneProvider: () -> TimezoneProvider,
) {
    private var cachedSnapshot: EnvironmentSnapshot? = null

    private val network: NetworkProvider by lazy { networkProvider() }
    private val power: PowerProvider by lazy { powerProvider() }
    private val device: DeviceProvider by lazy { deviceProvider() }
    private val locale: LocaleProvider by lazy { localeProvider() }
    private val timezone: TimezoneProvider by lazy { timezoneProvider() }

    /**
     * Returns the current environment snapshot by querying all providers.
     * The result is cached and also updated when [flow] emits.
     */
    fun current(): EnvironmentSnapshot {
        val snapshot = EnvironmentSnapshot(
            networkType = network.current(),
            powerState = power.current(),
            deviceClass = device.current(),
            locale = locale.current(),
            timezone = timezone.current(),
        )
        cachedSnapshot = snapshot
        return snapshot
    }

    /**
     * Returns the last cached snapshot, if any.
     * Use [current] to compute a fresh snapshot from providers.
     */
    fun lastCached(): EnvironmentSnapshot? = cachedSnapshot

    /**
     * Emits [EnvironmentSnapshot] whenever any underlying provider emits a new value.
     * Each emission updates the cached snapshot. Emits at least once when collected.
     */
    fun flow(): Flow<EnvironmentSnapshot> = combine(
        network.flow(),
        power.flow(),
        device.flow(),
        locale.flow(),
        timezone.flow(),
    ) { networkType, powerState, deviceClass, localeValue, timezoneValue ->
        EnvironmentSnapshot(
            networkType = networkType,
            powerState = powerState,
            deviceClass = deviceClass,
            locale = localeValue,
            timezone = timezoneValue,
        )
    }.onEach { cachedSnapshot = it }
}
