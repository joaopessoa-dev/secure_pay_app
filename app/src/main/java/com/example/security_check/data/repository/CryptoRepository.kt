package com.example.security_check.data.repository

import com.example.security_check.data.storage.SecurePreferences
import com.example.security_check.security.crypto.CryptoEngine
import com.example.security_check.security.crypto.CryptoResult
import javax.crypto.Cipher

class CryptoRepository(
    private val cryptoEngine: CryptoEngine,
    private val securePreferences: SecurePreferences
) {
    fun prepareEncryption(): CryptoResult {
        return cryptoEngine.prepareEncryption()
    }

    fun prepareDecryption(): CryptoResult {
        val storedData = securePreferences.getEncryptedData()
            ?: return CryptoResult.Error(
                IllegalStateException("No encrypted data found"),
                "No data to decrypt"
            )

        return cryptoEngine.prepareDecryption(storedData.second)
    }

    fun encryptAndSave(cipher: Cipher, plainText: String): CryptoResult {
        val result = cryptoEngine.finalizeEncryption(
            cipher,
            plainText.toByteArray(Charsets.UTF_8)
        )

        if (result is CryptoResult.EncryptSuccess) {
            securePreferences.saveEncryptedData(result.cipherText, result.iv)
        }

        return result
    }

    fun decryptStored(cipher: Cipher): CryptoResult {
        val storedData = securePreferences.getEncryptedData()
            ?: return CryptoResult.Error(
                IllegalStateException("No encrypted data found"),
                "No data to decrypt"
            )

        return cryptoEngine.finalizeDecryption(cipher, storedData.first)
    }

    fun hasStoredData(): Boolean {
        return securePreferences.hasStoredData()
    }
}
