package com.example.partsphere.presentation.owner.repository

import android.content.Context
import android.net.Uri
import com.example.partsphere.network.DistributorApi
import com.example.partsphere.presentation.owner.model.AddCOResponse
import com.example.partsphere.presentation.owner.model.CreateFactoryRequest
import com.example.partsphere.presentation.owner.model.CreateFactoryResponse
import com.example.partsphere.presentation.owner.model.EmployeeCount
import com.example.partsphere.presentation.owner.model.FactoryLocation
import com.example.partsphere.presentation.owner.model.FactoryResponse
import com.example.partsphere.presentation.owner.model.PlantHeadResponse
import com.example.partsphere.presentation.owner.model.UnassignedPlantHead
import com.example.partsphere.presentation.owner.model.UpdateCentralOfficerRequest
import com.example.partsphere.presentation.owner.model.UpdateFactoryRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class OwnerRepository @Inject constructor(
    private val api: DistributorApi,
    @ApplicationContext private val context: Context
) {
    suspend fun getEmployeeCounts(): List<EmployeeCount> {
        val response = api.getEmployeeCounts()
        if (response.isSuccessful) {
            val body = response.body()
            return body?.data?.map { dto ->
                EmployeeCount(
                    role = dto.role ,
                    count = dto.count
                )
            } ?: emptyList()
        } else {
            throw Exception("Failed to fetch employee counts: ${response.code()}")
        }
    }

    suspend fun getFactoryCountByLocation(): Result<List<FactoryLocation>> {
        return try {
            val response = api.getFactoryCountByLocation()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.status == "success") {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Invalid response"))
                }
            } else {
                Result.failure(Exception("API call failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFactories(page: Int = 0, size: Int = 5): FactoryResponse? {
        val response = api.getAllFactories(page, size)
        return if (response.isSuccessful) response.body() else null
    }

    // OwnerRepository.kt
    suspend fun updateFactory(factoryId: Int, request: UpdateFactoryRequest): Response<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            api.updateFactory(factoryId, request)
        }
    }

    suspend fun getUnassignedPlantHeads(): Response<List<UnassignedPlantHead>> {
        return withContext(Dispatchers.IO) {
            api.getUnassignedPlantHeads()
        }
    }

    suspend fun createFactory(name: String, location: String): Result<CreateFactoryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.createFactory(CreateFactoryRequest(name, location))
                if (response.isSuccessful) {
                    Result.success(response.body()!!)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Something went wrong"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: HttpException) {
                Result.failure(e)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteFactory(factoryId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.deleteFactory(factoryId)
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Factory deleted successfully!"
                    Result.success(message)
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Failed to delete factory"
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addCentralOfficer(
        name: String,
        email: String,
        photoUri: Uri?
    ): Result<AddCOResponse> = withContext(Dispatchers.IO) {
        try {
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())

            val photoPart: MultipartBody.Part? = photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }

                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)
            }

            val response: Response<AddCOResponse> =
                api.addCentralOfficer(namePart, emailPart, photoPart)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all central officers



    suspend fun getAllCentralOfficers(): Result<List<AddCOResponse>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getAllCentralOfficers()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteCentralOfficer(id: Int): Result<String> {
        return try {
            val response = api.deleteCentralOfficer(id)
            if (response.isSuccessful) {
                Result.success("Officer deleted successfully")
            } else {
                Result.failure(Exception("Failed to delete officer"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCentralOfficer(
        context: Context,
        officerId: Int,
        username: String,
        email: String,
        photoUri: Uri?
    ): Result<AddCOResponse> {
        return try {
            val idPart = officerId.toString().toRequestBody()
            val usernamePart = username.toRequestBody()
            val emailPart = email.toRequestBody()

            val photoPart = photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                    ?: return Result.failure(Exception("Cannot open image"))

                val bytes = inputStream.readBytes()
                val requestBody = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(
                    "photo",
                    "image.jpg", // name doesn't matter much
                    requestBody
                )
            }

            val response = api.updateCentralOfficer(idPart, usernamePart, emailPart, photoPart)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Failed to update officer"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



        suspend fun getPlantHeads(): List<PlantHeadResponse>? {
            val response = api.getPlantHeads()
            return if (response.isSuccessful) response.body() else null
        }






}






