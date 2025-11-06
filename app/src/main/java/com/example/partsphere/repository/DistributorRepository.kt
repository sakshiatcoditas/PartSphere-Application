package com.example.partsphere.repository



import com.example.partsphere.model.DistributorRegistrationRequest
import com.example.partsphere.model.DistributorRegistrationResponse
import com.example.partsphere.model.UserRegistrationData
import com.example.partsphere.network.DistributorApi

import retrofit2.Response

class DistributorRepository(private val api: DistributorApi) {

    suspend fun registerDistributor(user: UserRegistrationData): Result<DistributorRegistrationResponse> {
        return try {
            val request = DistributorRegistrationRequest(
                username = user.fullName,
                email = user.email,
                phoneNo = user.phoneNumber,
                companyName = user.companyName,
                gstId = user.gstId.takeIf { it.isNotBlank() },
                companyAddress = user.address,
                city = user.city,
                photo = user.photoUrl,  // optional
                state = user.state,
                pinCode = user.pincode.toIntOrNull() ?: 0,
                password = user.password
            )

            val response: Response<DistributorRegistrationResponse> = api.registerDistributor(request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                // Parse error body or return generic message
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
