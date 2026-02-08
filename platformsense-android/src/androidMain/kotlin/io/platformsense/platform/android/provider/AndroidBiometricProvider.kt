package io.platformsense.platform.android.provider

import android.content.Context
import android.os.Build
import io.platformsense.core.provider.BiometricProvider
import io.platformsense.domain.BiometricCapability
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Android implementation of [BiometricProvider] using [android.hardware.biometrics.BiometricManager] (API 29+).
 *
 * Maps [BiometricManager.canAuthenticate] to [BiometricCapability.isAvailable].
 */
class AndroidBiometricProvider(
    private val context: Context,
) : BiometricProvider {

    override fun current(): BiometricCapability = BiometricCapability(isAvailable = isBiometricAvailable())

    override fun flow(): Flow<BiometricCapability> = flowOf(current())

    private fun isBiometricAvailable(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return false
        val biometricManager = context.getSystemService(Context.BIOMETRIC_SERVICE) as? android.hardware.biometrics.BiometricManager
            ?: return false
        return biometricManager.canAuthenticate(android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
            android.hardware.biometrics.BiometricManager.BIOMETRIC_SUCCESS
    }
}
