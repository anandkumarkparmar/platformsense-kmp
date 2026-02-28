package io.github.anandkumarkparmar.platformsense.platform.android.provider

import android.content.Context
import android.hardware.biometrics.BiometricManager
import android.os.Build
import io.github.anandkumarkparmar.platformsense.core.models.capability.BiometricInfo
import io.github.anandkumarkparmar.platformsense.core.models.capability.BiometricStatus
import io.github.anandkumarkparmar.platformsense.core.models.capability.BiometricType
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Android implementation of [BiometricProvider] using [android.hardware.biometrics.BiometricManager] (API 29+).
 *
 * Maps [BiometricManager.canAuthenticate] to [BiometricInfo].
 */
class AndroidBiometricProvider(private val context: Context) : BiometricProvider {

    override fun current(): BiometricInfo = getBiometricInfo()

    override fun flow(): Flow<BiometricInfo> = flowOf(current())

    private fun getBiometricInfo(): BiometricInfo {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return BiometricInfo(status = BiometricStatus.NOT_SUPPORTED, type = BiometricType.UNKNOWN)
        }

        val biometricManager = context.getSystemService(Context.BIOMETRIC_SERVICE) as? BiometricManager
            ?: return BiometricInfo(status = BiometricStatus.NOT_SUPPORTED, type = BiometricType.UNKNOWN)

        val status = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NOT_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NOT_SUPPORTED
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.UNAVAILABLE
            else -> BiometricStatus.UNAVAILABLE
        }

        // Android does not expose the specific hardware type easily via public API until very recent versions
        // or manufacturer-specific APIs. Defaulting to MULTIPLE_OR_GENERIC when supported.
        val type = if (status !=
            BiometricStatus.NOT_SUPPORTED
        ) {
            BiometricType.MULTIPLE_OR_GENERIC
        } else {
            BiometricType.UNKNOWN
        }

        return BiometricInfo(status = status, type = type)
    }
}
