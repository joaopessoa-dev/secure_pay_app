package com.example.security_check.security.keystore

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class KeyManager {
    private val keyStore : KeyStore = KeyStore.getInstance(KeyStoreConfig.ANDROID_KEYSTORE_PROVIDER).apply {
        load(null)
    }

    val cipherTransformation = "AES/GCM/NoPadding"

    @RequiresApi(Build.VERSION_CODES.R)
    fun generateKey() : Result<SecretKey> {
        return try {
            val key = generateKeyWithSpec(KeyStoreConfig.createVaultKeySpecStrongBox())
            Result.success(key)
        } catch (e : Exception) {
            try {
                val key = generateKeyWithSpec(KeyStoreConfig.createVaultKeySpecNoStrongBox())
                Result.success(key)
            } catch (e : Exception) {
                Result.failure(e)
            }

        }
    }

    fun generateKeyWithSpec(spec : KeyGenParameterSpec) : SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KeyStoreConfig.ANDROID_KEYSTORE_PROVIDER
        )
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }

    fun getVaultKey() : SecretKey? {
        return try {
        keyStore.getKey(KeyStoreConfig.KEY_STORE_ALIAS,null) as SecretKey
        } catch (e : Exception){
            Log.d("get Vault Key", "getVaulKey: $e")
            null
        }
    }

    fun isVaultKeyValid() : Boolean  {
        val key = getVaultKey() ?: return false

        return try  {
            val cipher = Cipher.getInstance(cipherTransformation)
            cipher.init(Cipher.ENCRYPT_MODE,key)
            return true
        }catch (e : KeyPermanentlyInvalidatedException) {

            Log.e("isVaultKeyValid", "isVaultKeyValid: $e", )
            false
        } catch (e : Exception) {
            Log.e("isVaultKeyValid", "KeyPermanentlyInvalidatedException: $e", )
            false
        }
    }

    fun deleteVaultKey () : Boolean{
        return try {
            keyStore.deleteEntry(KeyStoreConfig.KEY_STORE_ALIAS)
            true
        } catch (e : Exception) {
            Log.e("deleteVaultKey", "deleteVaultKey: $e", )
            false
        }
    }
}