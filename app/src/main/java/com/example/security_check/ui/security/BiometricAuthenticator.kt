package com.example.security_check.ui.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.crypto.Cipher

class BiometricAuthenticator(
    private val activity: FragmentActivity
) {
    private var biometricPrompt: BiometricPrompt? = null

    fun authenticate(
        cipher: Cipher,
        onSuccess: (Cipher) -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                result.cryptoObject?.cipher?.let { authenticatedCipher ->
                    onSuccess(authenticatedCipher)
                } ?: onError("Failed to retrieve authenticated cipher")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val errorMessage = when (errorCode) {
                    BiometricPrompt.ERROR_CANCELED,
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> "Authentication cancelled"
                    BiometricPrompt.ERROR_LOCKOUT -> "Too many attempts. Try again later."
                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> "Biometric permanently locked. Use device credentials."
                    BiometricPrompt.ERROR_NO_BIOMETRICS -> "No biometric credentials enrolled"
                    BiometricPrompt.ERROR_HW_NOT_PRESENT -> "Biometric hardware not available"
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> "Biometric hardware unavailable"
                    else -> errString.toString()
                }
                onError(errorMessage)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
            }
        }

        biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate to access secure data")
            .setDescription("Use your fingerprint or face to encrypt/decrypt data")
            .setNegativeButtonText("Cancel")
            .setConfirmationRequired(true)
            .build()

        try {
            biometricPrompt?.authenticate(
                promptInfo,
                BiometricPrompt.CryptoObject(cipher)
            )
        } catch (e: Exception) {
            onError("Failed to start authentication: ${e.message}")
        }
    }

    fun cancelAuthentication() {
        biometricPrompt?.cancelAuthentication()
    }

    companion object {
        fun canAuthenticate(activity: FragmentActivity): BiometricStatus {
            val biometricManager = BiometricManager.from(activity)
            return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.Available
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NoHardware
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HardwareUnavailable
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NoneEnrolled
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> BiometricStatus.SecurityUpdateRequired
                else -> BiometricStatus.Unknown
            }
        }
    }
}

sealed class BiometricStatus {
    object Available : BiometricStatus()
    object NoHardware : BiometricStatus()
    object HardwareUnavailable : BiometricStatus()
    object NoneEnrolled : BiometricStatus()
    object SecurityUpdateRequired : BiometricStatus()
    object Unknown : BiometricStatus()

    fun isAvailable(): Boolean = this is Available

    fun getErrorMessage(): String? = when (this) {
        is Available -> null
        is NoHardware -> "Biometric hardware not available on this device"
        is HardwareUnavailable -> "Biometric hardware is currently unavailable"
        is NoneEnrolled -> "No biometric credentials enrolled. Please set up fingerprint or face unlock in device settings."
        is SecurityUpdateRequired -> "Security update required for biometric authentication"
        is Unknown -> "Biometric authentication status unknown"
    }
}
