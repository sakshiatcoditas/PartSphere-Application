package com.example.partsphere.presentation.login_screen


sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String = "", val role: String? = "") : AuthState()

    data class Error(val message: String) : AuthState()
}
