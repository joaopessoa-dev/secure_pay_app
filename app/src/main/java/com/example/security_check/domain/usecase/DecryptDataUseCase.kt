package com.example.security_check.domain.usecase

import com.example.security_check.data.repository.CryptoRepository
import com.example.security_check.security.crypto.CryptoResult
import javax.crypto.Cipher

class DecryptDataUseCase(
    private val repository: CryptoRepository
) {
    fun prepareDecryption(): CryptoResult {
        if (!repository.hasStoredData()) {
            return CryptoResult.Error(
                IllegalStateException("No data stored"),
                "No encrypted data to decrypt"
            )
        }
        return repository.prepareDecryption()
    }

    fun executeDecryption(cipher: Cipher): CryptoResult {
        return repository.decryptStored(cipher)
    }

    fun hasDataToDecrypt(): Boolean {
        return repository.hasStoredData()
    }
}
