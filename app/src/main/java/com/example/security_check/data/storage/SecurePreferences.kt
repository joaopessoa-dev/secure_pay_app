package com.example.security_check.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit

class SecurePreferences(
    context: Context
) {
    companion object {
        private const val PREFS_NAME = "secure_vault_prefs"
        private const val KEY_CIPHERTEXT = "encrypted_data"
        private const val KEY_IV = "encryption_iv"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveEncryptedData(cipherText: ByteArray, iv: ByteArray) {
        prefs.edit {
            putString(KEY_CIPHERTEXT, Base64.encodeToString(cipherText, Base64.NO_WRAP))
                .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
        }
    }

    fun getEncryptedData(): Pair<ByteArray, ByteArray>? {
        val cipherTextBase64 = prefs.getString(KEY_CIPHERTEXT, null) ?: return null
        val ivBase64 = prefs.getString(KEY_IV, null) ?: return null

        return try {
            val cipherText = Base64.decode(cipherTextBase64, Base64.NO_WRAP)
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
            Pair(cipherText, iv)
        } catch (e: Exception) {
            null
        }
    }

    fun hasStoredData(): Boolean {
        return prefs.contains(KEY_CIPHERTEXT) && prefs.contains(KEY_IV)
    }

    fun clearData() {
        prefs.edit {
            remove(KEY_CIPHERTEXT)
                .remove(KEY_IV)
        }
    }
}
