package com.example.partsphere.repository

import android.content.Context
import android.net.Uri
import com.example.partsphere.model.DistributorRegistrationResponse
import com.example.partsphere.model.UserRegistrationData
import com.example.partsphere.network.DistributorApi
import com.example.partsphere.utils.MultipartUtils
import retrofit2.Response

import javax.inject.Inject


class DistributorRepository @Inject constructor(
    private val api: DistributorApi
) {


    suspend fun registerDistributor(
        context: Context,
        user: UserRegistrationData
    ): Result<DistributorRegistrationResponse> {
        return try {


            val response: Response<DistributorRegistrationResponse> = api.registerDistributor(
                username = MultipartUtils.createPartFromString(user.username),
                email = MultipartUtils.createPartFromString(user.email),
                password = MultipartUtils.createPartFromString(user.password),
                phoneNo = MultipartUtils.createPartFromString(user.phoneNo),
                companyName = MultipartUtils.createPartFromString(user.companyName),
                companyAddress = MultipartUtils.createPartFromString(user.companyAddress),
                gstId = user.gstId.takeIf { it.isNotBlank() }?.let { MultipartUtils.createPartFromString(it) },
                state = MultipartUtils.createPartFromString(user.state),
                city = MultipartUtils.createPartFromString(user.city),
                pinCode = MultipartUtils.createPartFromString(user.pinCode.toString()),
                photo = user.photo?.let { MultipartUtils.prepareFilePart(context, "photo", it) }
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
