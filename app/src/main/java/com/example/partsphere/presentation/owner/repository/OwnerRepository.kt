package com.example.partsphere.presentation.owner.repository

import android.net.Uri
import com.example.partsphere.network.DistributorApi
import com.example.partsphere.presentation.owner.model.AddCOResponse
import com.example.partsphere.presentation.owner.model.CreateFactoryRequest
import com.example.partsphere.presentation.owner.model.CreateFactoryResponse
import com.example.partsphere.presentation.owner.model.EmployeeCount
import com.example.partsphere.presentation.owner.model.FactoryLocation
import com.example.partsphere.presentation.owner.model.FactoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class OwnerRepository @Inject constructor(
    private val api: DistributorApi
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
    suspend fun updateFactory(factoryId: Int, newName: String): Boolean {
        val requestBody = mapOf("name" to newName)
        val response = api.updateFactory(factoryId, requestBody)
        return response.isSuccessful
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

    suspend fun addCentralOfficer(name: String, email: String, photoUri: Uri?): Result<AddCOResponse> {
        return try {
            val namePart = RequestBody.create("text/plain".toMediaTypeOrNull(), name)
            val emailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), email)
            val photoPart = photoUri?.let {
                val file = File(it.path!!)
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
                MultipartBody.Part.createFormData("photo", file.name, requestFile)
            }

            val response = api.addCentralOfficer(namePart, emailPart, photoPart)
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
