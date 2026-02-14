package com.example.security_check.ui.home

import androidx.lifecycle.ViewModel
import com.example.security_check.security.crypto.CryptoEngine
import com.example.security_check.security.crypto.CryptoResult
import javax.crypto.Cipher

class HomeViewModel (
    private val cryptoEngine : CryptoEngine
) : ViewModel() {

    fun prepareSecureOperation() : CryptoResult {
        return cryptoEngine.prepareEncryption()
    }

    fun finalizeSecureOperation (
        cipher : Cipher,
        byteArray: ByteArray
    ) : CryptoResult {
        return cryptoEngine.finalizeDecryption(cipher,byteArray)
    }
}