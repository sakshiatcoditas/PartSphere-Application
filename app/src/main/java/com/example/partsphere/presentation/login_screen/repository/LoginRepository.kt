package com.example.partsphere.presentation.login_screen.repository

import com.example.partsphere.network.DistributorApi
import com.example.partsphere.presentation.login_screen.model.LoginRequest
import com.example.partsphere.presentation.login_screen.model.LoginResponse
import retrofit2.Response
import javax.inject.Inject

class LoginRepository @Inject constructor(private val apiService: DistributorApi) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val request = LoginRequest(email, password)
        return apiService.loginUser(request)
    }
}