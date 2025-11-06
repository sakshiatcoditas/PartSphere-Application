package com.example.partsphere.repository

import android.content.Context
import android.net.Uri
import com.example.partsphere.model.DistributorRegistrationResponse
import com.example.partsphere.model.UserRegistrationData
import com.example.partsphere.network.DistributorApi
import com.example.partsphere.utils.MultipartUtils
import retrofit2.Response
import androidx.core.net.toUri
import javax.inject.Inject
import okhttp3.RequestBody
import okhttp3.MultipartBody

class DistributorRepository @Inject constructor(
    private val api: DistributorApi
) {


    suspend fun registerDistributor(
        context: Context,
        user: UserRegistrationData
    ): Result<DistributorRegistrationResponse> {
        return try {
            // ----------- LOGGING THE FIELDS -----------
            android.util.Log.d("RegistrationRepo", "Sending registration data:")
            android.util.Log.d("RegistrationRepo", "username: ${user.username}")
            android.util.Log.d("RegistrationRepo", "email: ${user.email}")
            android.util.Log.d("RegistrationRepo", "password: ${user.password}")
            android.util.Log.d("RegistrationRepo", "phoneNo: ${user.phoneNo}")
            android.util.Log.d("RegistrationRepo", "companyName: ${user.companyName}")
            android.util.Log.d("RegistrationRepo", "companyAddress: ${user.companyAddress}")
            android.util.Log.d("RegistrationRepo", "gstId: ${user.gstId}")
            android.util.Log.d("RegistrationRepo", "state: ${user.state}")
            android.util.Log.d("RegistrationRepo", "city: ${user.city}")
            android.util.Log.d("RegistrationRepo", "pinCode: ${user.pinCode}")
            android.util.Log.d("RegistrationRepo", "photo: ${user.photo?.path ?: "null"}")
            // -----------------------------------------

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
