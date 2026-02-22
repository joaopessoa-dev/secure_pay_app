package com.example.security_check

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.security_check.data.repository.CryptoRepository
import com.example.security_check.data.storage.SecurePreferences
import com.example.security_check.domain.usecase.DecryptDataUseCase
import com.example.security_check.domain.usecase.EncryptDataUseCase
import com.example.security_check.security.crypto.CryptoEngine
import com.example.security_check.security.keystore.KeyManager
import com.example.security_check.ui.home.HomeScreen
import com.example.security_check.ui.home.HomeViewModel
import com.example.security_check.ui.security.BiometricAuthenticator
import com.example.security_check.ui.security.BiometricStatus

class MainActivity : FragmentActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var biometricAuthenticator: BiometricAuthenticator
    private lateinit var keyManager: KeyManager

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeDependencies()
        checkBiometricAvailability()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    HomeScreen(
                        viewModel = viewModel,
                        onBiometricAuthRequired = {
                            uiState.pendingCipher?.let { cipher ->
                                biometricAuthenticator.authenticate(
                                    cipher = cipher,
                                    onSuccess = { authenticatedCipher ->
                                        viewModel.onBiometricSuccess(authenticatedCipher)
                                    },
                                    onError = { error ->
                                        viewModel.onBiometricError(error)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initializeDependencies() {
        keyManager = KeyManager()

        if (keyManager.getVaultKey() == null) {
            val result = keyManager.generateKey()
            if (result.isFailure) {
                Toast.makeText(
                    this,
                    "Failed to generate security key: ${result.exceptionOrNull()?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val cryptoEngine = CryptoEngine(keyManager)
        val securePreferences = SecurePreferences(this)
        val cryptoRepository = CryptoRepository(cryptoEngine, securePreferences)

        val encryptDataUseCase = EncryptDataUseCase(cryptoRepository)
        val decryptDataUseCase = DecryptDataUseCase(cryptoRepository)

        viewModel = HomeViewModel(encryptDataUseCase, decryptDataUseCase)
        biometricAuthenticator = BiometricAuthenticator(this)
    }

    private fun checkBiometricAvailability() {
        when (val status = BiometricAuthenticator.canAuthenticate(this)) {
            is BiometricStatus.Available -> {
                Toast.makeText(this,"Biometric Authenticator is ok", Toast.LENGTH_LONG).show()
            }
            else -> {
                status.getErrorMessage()?.let { errorMessage ->
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        biometricAuthenticator.cancelAuthentication()
    }
}
