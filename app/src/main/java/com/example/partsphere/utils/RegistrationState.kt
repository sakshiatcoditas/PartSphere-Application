package com.example.partsphere.utils

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val field: String) : RegistrationState()
}
