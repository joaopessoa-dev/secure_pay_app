package com.example.security_check.Security.crypto

import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.UserNotAuthenticatedException
import com.example.security_check.Security.keystore.KeyManager
import java.security.InvalidKeyException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class CryptoEngine(
    private val keyManager: KeyManager
) {

    companion object  {
        const val GCM_BITS_TAG_LENGTH = 128
    }

    fun prepareEncryption() : CryptoResult? {
        val key = keyManager.getVaultKey() ?: return CryptoResult.KeyNotFound

        return try {
            val cipher = createCipher()
            cipher.init(Cipher.ENCRYPT_MODE,key)

            CryptoResult.AuthRequired(cipher)
        }catch (e : UserNotAuthenticatedException)  {
            try {
                val cipher = createCipher()
                initCipherForAuth(cipher,key)

            }catch (e : KeyPermanentlyInvalidatedException) {
                CryptoResult.KeyInvalidated
            }catch (e : Exception) {
                CryptoResult.Error(e)
            }
        }catch (e : KeyPermanentlyInvalidatedException)  {
            return CryptoResult.KeyInvalidated
        }catch (e : InvalidKeyException) {
            return CryptoResult.Error(e,"Invalid State Of Key")
        }catch(e : Exception) {
            return CryptoResult.Error(e)
        }
    }

    fun prepareDecryption(iv : ByteArray) : CryptoResult {
        val key = keyManager.getVaultKey() ?: return CryptoResult.KeyNotFound

        return try {
            val cipher = createCipher()
            val spec = GCMParameterSpec(GCM_BITS_TAG_LENGTH,iv)
            cipher.init(Cipher.DECRYPT_MODE,key,spec)
            CryptoResult.AuthRequired(cipher)
        }catch(e : UserNotAuthenticatedException)  {
            try {
                val cipher = createCipher()
                initCipherForAuth(cipher,key)
            }catch (e : Exception)  {
                CryptoResult.Error(e)
            }catch(e : KeyPermanentlyInvalidatedException) {
                CryptoResult.KeyInvalidated
            }
        }catch (e : KeyPermanentlyInvalidatedException) {
            CryptoResult.KeyInvalidated
        } catch (e : Exception) {
            CryptoResult.Error(e)
        }
    }

    fun createCipher() : Cipher {
        return Cipher.getInstance(keyManager.cipherTransformation)
    }

    fun initCipherForAuth(cipher : Cipher, key : SecretKey) : CryptoResult {
        return try {
            cipher.init(Cipher.ENCRYPT_MODE,key)
            CryptoResult.AuthRequired(cipher)
        } catch (e : KeyPermanentlyInvalidatedException) {
            CryptoResult.KeyInvalidated
        }catch (e : Exception) {
            CryptoResult.Error(e)
        }
    }

}