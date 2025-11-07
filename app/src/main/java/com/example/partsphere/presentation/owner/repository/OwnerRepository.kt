package com.example.partsphere.presentation.owner.repository

import com.example.partsphere.network.DistributorApi
import com.example.partsphere.presentation.owner.model.EmployeeCount
import com.example.partsphere.presentation.owner.model.FactoryLocation
import com.example.partsphere.presentation.owner.model.FactoryResponse
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

}
