# 🔐 SecurePay Vault

SecurePay Vault is a native Android application that demonstrates **enterprise-grade security practices** for encrypting and storing sensitive data. Built with modern Android development standards and strict Clean Architecture, the app showcases how to properly implement **biometric-bound cryptographic operations** using the Android Keystore system.

This project serves both as a **production-grade security reference** and a **learning resource** for secure Android development.

---

## 📱 Overview

SecurePay Vault simulates secure storage and cryptographic authorization flows using:

- Hardware-backed key storage
- AES-256 GCM authenticated encryption
- Fingerprint-only biometric protection
- Secure local persistence
- Clean Architecture with strict layer separation

The goal is to demonstrate how to handle sensitive data in a real-world financial-grade Android application.

---

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture (Domain / Data / UI) + MVVM
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 36
- **Security Libraries:**
  - AndroidX Biometric
  - AndroidX Security Crypto

---

## 🔐 Key Security Features

### 1️⃣ AES-256 GCM Encryption
- Industry-standard symmetric encryption
- 256-bit key size
- GCM mode (Authenticated Encryption with Associated Data)
- Automatic IV generation per operation
- Protection against tampering via authentication tags

---

### 2️⃣ Android Keystore Integration
- Hardware-backed key storage
- StrongBox support (with automatic fallback)
- Keys never leave secure hardware
- Device unlock required before key access

---

### 3️⃣ Biometric Authentication (Fingerprint-Only)
- Class 3 (Strong) biometric requirement
- Fingerprint-only enforcement
- Keys invalidated when biometric enrollment changes
- Zero authentication validity timeout  
  → User must authenticate for **every cryptographic operation**

---

### 4️⃣ Secure Data Persistence
- Encrypted data stored in SharedPreferences
- Base64 encoding for ciphertext and IV storage
- Strict separation of:
  - Encrypted content
  - Initialization Vector (IV)
  - Cryptographic metadata

---

Each layer has a single responsibility and depends only on abstractions from inner layers.

---

## ✅ Security Best Practices Implemented

- 🔒 User authentication required for every cryptographic operation
- 🔄 Key invalidation when biometric enrollment changes
- 📱 Device unlock requirement before key usage
- 🛡 StrongBox hardware support with automatic fallback
- 🧩 Sealed class result handling for type-safe error management
- 🚫 No hardcoded secrets
- 🔐 Keys generated and stored inside secure hardware only

---

## 🎯 Use Cases

SecurePay Vault can serve as a foundation for:

- Secure password and credential storage
- Financial transaction authorization
- Offline token validation
- Sensitive document encryption
- Healthcare/HIPAA-compliant local data storage
- Any Android app requiring biometric-protected encryption

---

## 🎓 Learning Outcomes

This project demonstrates:

- Proper Android Keystore usage with biometric binding
- Secure AES-256 GCM implementation
- StrongBox-aware key configuration
- Clean Architecture implementation in Android
- Jetpack Compose UI with reactive state management
- Kotlin Coroutines and StateFlow
- Enterprise-level mobile security practices

---

## 🚀 Why This Project Matters

Many Android apps misuse cryptography by:

- Storing raw keys in memory
- Reusing IVs
- Using insecure cipher modes (like ECB)
- Not binding keys to biometric authentication

SecurePay Vault demonstrates how to implement **correct, production-ready mobile cryptography** aligned with fintech-level security standards.

---

## 📌 Keywords

Android Security · Biometric Authentication · AES-256 · Android Keystore · StrongBox · Jetpack Compose · Kotlin · Clean Architecture · MVVM · Mobile Security · Fingerprint Authentication


> This project is intended for educational and professional demonstration purposes.
