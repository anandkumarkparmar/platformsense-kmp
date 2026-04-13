package io.github.anandkumarkparmar.platformsense.platform.ios.provider

import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricInfo
import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricStatus
import io.github.anandkumarkparmar.platformsense.core.models.device.BiometricType
import io.github.anandkumarkparmar.platformsense.core.provider.BiometricProvider
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSError
import platform.LocalAuthentication.LABiometryTypeFaceID
import platform.LocalAuthentication.LABiometryTypeTouchID
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorBiometryLockout
import platform.LocalAuthentication.LAErrorBiometryNotAvailable
import platform.LocalAuthentication.LAErrorBiometryNotEnrolled
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics

@OptIn(ExperimentalForeignApi::class)
class IosBiometricProvider : BiometricProvider {

    override fun current(): BiometricInfo = getBiometricInfo()

    override fun flow(): Flow<BiometricInfo> = flowOf(current())

    private fun getBiometricInfo(): BiometricInfo {
        val context = LAContext()
        val status = memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val success = context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, error.ptr)

            val err = error.value
            if (success) {
                BiometricStatus.READY
            } else if (err != null) {
                // In Kotlin Native, err.code is an NSInteger. The LAError enum values are also integers.
                when (err.code) {
                    LAErrorBiometryNotEnrolled -> BiometricStatus.NOT_ENROLLED
                    LAErrorBiometryLockout -> BiometricStatus.UNAVAILABLE
                    LAErrorBiometryNotAvailable -> BiometricStatus.NOT_SUPPORTED
                    else -> BiometricStatus.UNAVAILABLE
                }
            } else {
                BiometricStatus.NOT_SUPPORTED
            }
        }

        val type = when (context.biometryType) {
            LABiometryTypeFaceID -> BiometricType.FACE
            LABiometryTypeTouchID -> BiometricType.FINGERPRINT
            else -> BiometricType.UNKNOWN
        }

        return BiometricInfo(status = status, type = type)
    }
}
