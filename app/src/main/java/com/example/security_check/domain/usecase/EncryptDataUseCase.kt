package com.example.security_check.domain.usecase

import com.example.security_check.data.repository.CryptoRepository
import com.example.security_check.security.crypto.CryptoResult
import javax.crypto.Cipher

class EncryptDataUseCase(
    private val repository: CryptoRepository
) {
    fun prepareEncryption(): CryptoResult {
        return repository.prepareEncryption()
    }

    fun executeEncryption(cipher: Cipher, plainText: String): CryptoResult {
        if (plainText.isBlank()) {
            return CryptoResult.Error(
                IllegalArgumentException("Text cannot be empty"),
                "Please enter text to encrypt"
            )
        }
        return repository.encryptAndSave(cipher, plainText)
    }
}
