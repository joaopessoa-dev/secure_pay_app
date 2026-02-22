package com.example.security_check.security.crypto

import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import com.example.security_check.security.keystore.KeyManager
import java.security.InvalidKeyException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

class CryptoEngine(
    private val keyManager: KeyManager
) {

    companion object  {
        const val GCM_BITS_TAG_LENGTH = 128
    }

    fun prepareEncryption(): CryptoResult {
        val key = keyManager.getVaultKey()
            ?: return CryptoResult.KeyNotFound

        return try {
            val cipher = Cipher.getInstance(keyManager.cipherTransformation)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            CryptoResult.AuthRequired(cipher)

        } catch (e: UserNotAuthenticatedException) {
            Log.e("User No Auth", "prepareEncryption: $e")
            CryptoResult.UserNotAuth
        } catch (e: KeyPermanentlyInvalidatedException) {
            Log.e("KeyPermanentlyInvalidatedException", "prepareEncryption: $e")
            CryptoResult.KeyInvalidated
        } catch (e: InvalidKeyException) {
            Log.e("InvalidKeyException", "prepareEncryption: $e")
            CryptoResult.KeyInvalidated
        } catch (e: Exception) {
            Log.e("Unknow Error", "prepareEncryption: $e")
            CryptoResult.Error(e)
        }
    }

    fun prepareDecryption(iv: ByteArray): CryptoResult {
        val key = keyManager.getVaultKey()
            ?: return CryptoResult.KeyNotFound

        return try {
            val cipher = Cipher.getInstance(keyManager.cipherTransformation)
            val spec = GCMParameterSpec(GCM_BITS_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            CryptoResult.AuthRequired(cipher)

        } catch (e: UserNotAuthenticatedException) {
            Log.e("User No Auth", "prepareEncryption: $e")
            CryptoResult.UserNotAuth
        } catch (e: KeyPermanentlyInvalidatedException) {
            Log.e("KeyPermanentlyInvalidatedException", "prepareEncryption: $e")

            CryptoResult.KeyInvalidated
        } catch (e: Exception) {
            Log.e("Unknow Error", "prepareEncryption: $e")
            CryptoResult.Error(e)
        }
    }

    fun finalizeEncryption(cipher: Cipher, data: ByteArray): CryptoResult {
        return try {
            val cipherText = cipher.doFinal(data)
            CryptoResult.EncryptSuccess(
                cipherText = cipherText,
                iv = cipher.iv
            )
        } catch (e: Exception) {
            CryptoResult.Error(e)
        }
    }

    fun finalizeDecryption(cipher: Cipher, data: ByteArray): CryptoResult {
        return try {
            val plainText = cipher.doFinal(data)
            CryptoResult.DecryptSuccess(plainText)
        } catch (e: Exception) {
            CryptoResult.Error(e)
        }
    }

}