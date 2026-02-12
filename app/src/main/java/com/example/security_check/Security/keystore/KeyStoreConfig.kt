package com.example.security_check.Security.keystore

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi


object KeyStoreConfig {

    const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    const val KEY_STORE_ALIAS = "securepay_result_key"

    private const val SIZE = 256

    private const val AUTH_VALIDITY = 0

    @RequiresApi(Build.VERSION_CODES.R)
    fun createVaultKeySpecStrongBox() : KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            KEY_STORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setKeySize(SIZE)
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setIsStrongBoxBacked(true)
            setUserAuthenticationRequired(true)
            setUserAuthenticationParameters(
                AUTH_VALIDITY,
                KeyProperties.AUTH_BIOMETRIC_STRONG
            )
            setInvalidatedByBiometricEnrollment(true)
            setUnlockedDeviceRequired(true)
        }.build()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun createVaultKeySpecNoStrongBox() : KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            KEY_STORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setKeySize(SIZE)
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setUserAuthenticationRequired(true)
            setUserAuthenticationParameters(
                AUTH_VALIDITY,
                KeyProperties.AUTH_BIOMETRIC_STRONG
            )
            setInvalidatedByBiometricEnrollment(true)
            setUnlockedDeviceRequired(true)
        }.build()
    }
}

// define a key gen params for keystore
// fallback if failed