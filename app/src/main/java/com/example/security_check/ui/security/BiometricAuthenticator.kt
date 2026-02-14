package com.example.security_check.ui.security

import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.security_check.security.crypto.CryptoResult
import javax.crypto.Cipher

class BiometricAuthenticator (
    private val activity : FragmentActivity
) {

    fun authenticate(
        cipher: Cipher,
        onSuccess: (Cipher) -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        // todo implementation

    }
}