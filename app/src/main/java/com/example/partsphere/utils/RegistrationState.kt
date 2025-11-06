package com.example.partsphere.utils

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState() // registration successful
    data class Error(val field: Field) : RegistrationState() // field that has error
}
