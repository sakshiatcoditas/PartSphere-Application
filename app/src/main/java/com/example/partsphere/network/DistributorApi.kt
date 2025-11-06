package com.example.partsphere.network

import com.example.partsphere.model.DistributorRegistrationResponse
import com.example.partsphere.presentation.login_screen.model.LoginRequest
import com.example.partsphere.presentation.login_screen.model.LoginResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DistributorApi {

    @Multipart
    @POST("/api/auth/signup")
    suspend fun registerDistributor(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("phoneNo") phoneNo: RequestBody,
        @Part("companyName") companyName: RequestBody,
        @Part("companyAddress") companyAddress: RequestBody,
        @Part("gstId") gstId: RequestBody?,
        @Part("state") state: RequestBody,
        @Part("city") city: RequestBody,
        @Part("pinCode") pinCode: RequestBody,
        @Part photo: MultipartBody.Part? = null
    ): Response<DistributorRegistrationResponse>

    @POST("/api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}
