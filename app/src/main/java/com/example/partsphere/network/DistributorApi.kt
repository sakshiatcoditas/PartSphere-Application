package com.example.partsphere.network



import com.example.partsphere.model.DistributorRegistrationRequest
import com.example.partsphere.model.DistributorRegistrationResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.POST


interface DistributorApi {
    @POST("/api/auth/signup")
    suspend fun registerDistributor(
        @Body request: DistributorRegistrationRequest
    ): Response<DistributorRegistrationResponse>
}
