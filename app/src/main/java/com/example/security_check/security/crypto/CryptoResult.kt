package com.example.security_check.security.crypto

import javax.crypto.Cipher

sealed class CryptoResult {

    data class DecryptSuccess(
        val plainText: ByteArray
    ) : CryptoResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as DecryptSuccess

            return plainText.contentEquals(other.plainText)
        }

        override fun hashCode(): Int {
            return plainText.contentHashCode()
        }
    }

    data class EncryptSuccess(
        val cipherText: ByteArray,
        val iv : ByteArray
    ) : CryptoResult() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EncryptSuccess

            if (!cipherText.contentEquals(other.cipherText)) return false
            if (!iv.contentEquals(other.iv)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = cipherText.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            return result
        }

    }

    object KeyInvalidated : CryptoResult()

    object KeyNotFound : CryptoResult()

    data class AuthRequired (
        val cipher : Cipher
    ) : CryptoResult()

    data class Error(
        val exception: Exception,
        val msg : String = exception.message ?: "Unknown error"
    ) : CryptoResult()
}