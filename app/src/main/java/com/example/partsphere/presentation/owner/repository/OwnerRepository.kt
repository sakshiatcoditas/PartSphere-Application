package com.example.partsphere.presentation.owner.repository

import com.example.partsphere.network.DistributorApi
import com.example.partsphere.presentation.owner.model.CreateFactoryRequest
import com.example.partsphere.presentation.owner.model.CreateFactoryResponse
import com.example.partsphere.presentation.owner.model.EmployeeCount
import com.example.partsphere.presentation.owner.model.FactoryLocation
import com.example.partsphere.presentation.owner.model.FactoryResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
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




}
