package com.example.security_check.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.security_check.domain.usecase.DecryptDataUseCase
import com.example.security_check.domain.usecase.EncryptDataUseCase
import com.example.security_check.security.crypto.CryptoResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.crypto.Cipher

data class HomeUiState(
    val inputText: String = "",
    val decryptedText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasStoredData: Boolean = false,
    val operationType: OperationType? = null,
    val pendingCipher: Cipher? = null,
    val showBiometricPrompt: Boolean = false,
    val encryptionSuccess: Boolean = false
)

enum class OperationType {
    ENCRYPT,
    DECRYPT
}

class HomeViewModel(
    private val encryptDataUseCase: EncryptDataUseCase,
    private val decryptDataUseCase: DecryptDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        checkStoredData()
    }

    private fun checkStoredData() {
        _uiState.update { it.copy(hasStoredData = decryptDataUseCase.hasDataToDecrypt()) }
    }

    fun updateInputText(text: String) {
        _uiState.update {
            it.copy(
                inputText = text,
                errorMessage = null,
                encryptionSuccess = false
            )
        }
    }

    fun prepareEncryption() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = encryptDataUseCase.prepareEncryption()) {
                is CryptoResult.AuthRequired -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            operationType = OperationType.ENCRYPT,
                            pendingCipher = result.cipher,
                            showBiometricPrompt = true
                        )
                    }
                }
                is CryptoResult.KeyNotFound -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Security key not found. Please set up biometric authentication."
                        )
                    }
                }
                is CryptoResult.KeyInvalidated -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Security key invalidated. Please re-authenticate."
                        )
                    }
                }
                is CryptoResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.msg
                        )
                    }
                }
                is CryptoResult.UserNotAuth -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "User Not Authenticated with Finger Print. Authenticate with finger print on your phone"
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun prepareDecryption() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = decryptDataUseCase.prepareDecryption()) {
                is CryptoResult.AuthRequired -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            operationType = OperationType.DECRYPT,
                            pendingCipher = result.cipher,
                            showBiometricPrompt = true
                        )
                    }
                }
                is CryptoResult.KeyNotFound -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Security key not found."
                        )
                    }
                }
                is CryptoResult.KeyInvalidated -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Security key invalidated."
                        )
                    }
                }
                is CryptoResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.msg
                        )
                    }
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onBiometricSuccess(cipher: Cipher) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showBiometricPrompt = false) }

            val currentState = _uiState.value

            when (currentState.operationType) {
                OperationType.ENCRYPT -> {
                    when (val result = encryptDataUseCase.executeEncryption(cipher, currentState.inputText)) {
                        is CryptoResult.EncryptSuccess -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    inputText = "",
                                    encryptionSuccess = true,
                                    hasStoredData = true,
                                    operationType = null,
                                    pendingCipher = null
                                )
                            }
                        }
                        is CryptoResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.msg,
                                    operationType = null,
                                    pendingCipher = null
                                )
                            }
                        }
                        else -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    operationType = null,
                                    pendingCipher = null
                                )
                            }
                        }
                    }
                }
                OperationType.DECRYPT -> {
                    when (val result = decryptDataUseCase.executeDecryption(cipher)) {
                        is CryptoResult.DecryptSuccess -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    decryptedText = String(result.plainText, Charsets.UTF_8),
                                    operationType = null,
                                    pendingCipher = null
                                )
                            }
                        }
                        is CryptoResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.msg,
                                    operationType = null,
                                    pendingCipher = null
                                )
                            }
                        }
                        else -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    operationType = null,
                                    pendingCipher = null
                                )
                            }
                        }
                    }
                }
                null -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onBiometricError(error: String) {
        _uiState.update {
            it.copy(
                isLoading = false,
                showBiometricPrompt = false,
                errorMessage = error,
                operationType = null,
                pendingCipher = null
            )
        }
    }

    fun dismissBiometricPrompt() {
        _uiState.update {
            it.copy(
                showBiometricPrompt = false,
                operationType = null,
                pendingCipher = null
            )
        }
    }

    fun clearDecryptedText() {
        _uiState.update { it.copy(decryptedText = "") }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
