package com.example.partsphere.presentation.login_screen.model

data class LoginResponse(
    val message: String? = null,
    val uid: Int? = null,
    val username: String? = null,
    val email: String? = null,
    val role: String? = null,
    val token: String? = null,
    val error: String? = null
)