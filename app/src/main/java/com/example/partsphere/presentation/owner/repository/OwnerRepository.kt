package com.example.partsphere.presentation.owner.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.partsphere.network.DistributorApi
import com.example.partsphere.presentation.owner.model.AddCOResponse
import com.example.partsphere.presentation.owner.model.AddChiefSupervisorResponse
import com.example.partsphere.presentation.owner.model.AddPlantHeadResponse
import com.example.partsphere.presentation.owner.model.CreateFactoryRequest
import com.example.partsphere.presentation.owner.model.CreateFactoryResponse
import com.example.partsphere.presentation.owner.model.EmployeeCount
import com.example.partsphere.presentation.owner.model.FactoryLocation
import com.example.partsphere.presentation.owner.model.FactoryResponse
import com.example.partsphere.presentation.owner.model.PlantHeadPaginatedResponse
import com.example.partsphere.presentation.owner.model.PlantHeadResponse
import com.example.partsphere.presentation.owner.model.Product
import com.example.partsphere.presentation.owner.model.ProductResponse
import com.example.partsphere.presentation.owner.model.SupervisorFactory
import com.example.partsphere.presentation.owner.model.UnassignedFactoryItem
import com.example.partsphere.presentation.owner.model.UnassignedPlantHead
import com.example.partsphere.presentation.owner.model.UpdateFactoryRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
                    role = dto.role,
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

        Log.d("FACTORY_API", "HTTP Code: ${response.code()}")
        Log.d("FACTORY_API", "isSuccessful: ${response.isSuccessful}")
        Log.d("FACTORY_API", "Body: ${response.body()}")
        Log.d("FACTORY_API", "ErrorBody: ${response.errorBody()?.string()}")

        return if (response.isSuccessful) response.body() else null
    }


    suspend fun updateFactory(
        factoryId: Int,
        request: UpdateFactoryRequest
    ): Response<Map<String, String>> {
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
                val tempFile =
                    File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }

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


    private var currentPage = 0
    private val pageSize = 3
    private var isLastPage = false

    suspend fun getAllCentralOfficers(loadAll: Boolean = false): Result<List<AddCOResponse>> =
        withContext(Dispatchers.IO) {
            try {
                if (loadAll) {
                    currentPage = 0
                    isLastPage = false
                }

                val allOfficers = mutableListOf<AddCOResponse>()

                if (loadAll) {
                    do {
                        val response = api.getAllCentralOfficers(currentPage, pageSize)
                        if (response.isSuccessful && response.body() != null) {
                            val paginatedResponse = response.body()!!
                            allOfficers.addAll(paginatedResponse.content)
                            currentPage = paginatedResponse.number + 1
                            isLastPage = paginatedResponse.last
                        } else {
                            return@withContext Result.failure(
                                Exception(
                                    response.errorBody()?.string() ?: "Unknown error"
                                )
                            )
                        }
                    } while (!isLastPage)
                }
                // Load only next page during pagination
                else {
                    if (isLastPage) return@withContext Result.success(emptyList())

                    val response = api.getAllCentralOfficers(currentPage, pageSize)
                    if (response.isSuccessful && response.body() != null) {
                        val paginatedResponse = response.body()!!
                        currentPage = paginatedResponse.number + 1
                        isLastPage = paginatedResponse.last
                        allOfficers.addAll(paginatedResponse.content)
                    } else {
                        return@withContext Result.failure(
                            Exception(
                                response.errorBody()?.string() ?: "Unknown error"
                            )
                        )
                    }
                }

                Result.success(allOfficers)
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
                Result.failure(
                    Exception(
                        response.errorBody()?.string() ?: "Failed to update officer"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getPlantHeads(page: Int, size: Int): PlantHeadPaginatedResponse? {
        val response = api.getPlantHeads(page, size)
        return if (response.isSuccessful) response.body() else null
    }





    private var currentChiefPage = 0
    private val chiefPageSize = 3
    private var isLastChiefPage = false

    suspend fun getAllChiefSupervisors(loadAll: Boolean = false): Result<List<AddChiefSupervisorResponse>> =
        withContext(Dispatchers.IO) {
            try {
                if (loadAll) {
                    currentChiefPage = 0
                    isLastChiefPage = false
                }

                val allChiefs = mutableListOf<AddChiefSupervisorResponse>()

                // Load all pages on app start
                if (loadAll) {
                    do {
                        val response = api.getAllChiefSupervisors(currentChiefPage, chiefPageSize)
                        Log.d("GetChiefSupervisors", "Response code: ${response.code()}")
                        Log.d("GetChiefSupervisors", "Response body: ${response.body()}")
                        Log.d("GetChiefSupervisors", "Error body: ${response.errorBody()?.string()}")
                        if (response.isSuccessful && response.body() != null) {
                            val paginatedResponse = response.body()!!
                            allChiefs.addAll(paginatedResponse.content)
                            currentChiefPage = paginatedResponse.number + 1
                            isLastChiefPage = paginatedResponse.last
                        } else {
                            return@withContext Result.failure(
                                Exception(response.errorBody()?.string() ?: "Unknown error")
                            )
                        }
                    } while (!isLastChiefPage)
                } else {
                    // Only next page for pagination
                    if (isLastChiefPage) return@withContext Result.success(emptyList())

                    val response = api.getAllChiefSupervisors(currentChiefPage, chiefPageSize)
                    if (response.isSuccessful && response.body() != null) {
                        val paginatedResponse = response.body()!!
                        currentChiefPage = paginatedResponse.number + 1
                        isLastChiefPage = paginatedResponse.last
                        allChiefs.addAll(paginatedResponse.content)
                    } else {
                        return@withContext Result.failure(
                            Exception(response.errorBody()?.string() ?: "Unknown error")
                        )
                    }
                }

                Result.success(allChiefs)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    suspend fun getFactoriesForSupervisor(): Result<List<SupervisorFactory>> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getAllFactoriesForSupervisor()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.data)
                } else {
                    Result.failure(
                        Exception(response.errorBody()?.string() ?: "Failed to fetch factories")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }


    // Add Chief Supervisor
    suspend fun addChiefSupervisor(
        name: String,
        email: String,
        factoryId: Long,
        photoUri: Uri?
    ): Result<AddChiefSupervisorResponse> = withContext(Dispatchers.IO) {
        try {
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val factoryPart = factoryId.toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val rolePart = "CHIEF_SUPERVISOR".toRequestBody("text/plain".toMediaTypeOrNull())

            val photoPart: MultipartBody.Part? = photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile = File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input -> tempFile.outputStream().use { output -> input.copyTo(output) } }

                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)
            }

            val response = api.addChiefSupervisor(
                namePart,
                emailPart,
                factoryPart,
                rolePart,
                photoPart
            )
            Log.d("AddChiefSupervisor", "Response code: ${response.code()}")
            Log.d("AddChiefSupervisor", "Response body: ${response.body()?.toString()}")
            Log.d("AddChiefSupervisor", "Error body: ${response.errorBody()?.string()}")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllProducts(page: Int, size: Int): Result<ProductResponse> {
        return try {
            val response = api.getAllProducts(page, size)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: Int): Result<String> {
        return try {
            val response = api.deleteProduct(productId)
            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Product deleted successfully"
                Result.success(message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to delete product"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getUnassignedFactories(): Result<List<UnassignedFactoryItem>> {
        return try {
            val response = api.getUnassignedFactories()
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.status == "success") {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("API returned status: ${body.status}"))
                }
            } else {
                Result.failure(Exception("Failed to fetch factories: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addPlantHead(
        username: String,
        email: String,
        role: String,
        factoryId: Int,
        photoUri: Uri? = null
    ): Result<AddPlantHeadResponse> {
        return try {
            val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val roleBody = role.toRequestBody("text/plain".toMediaTypeOrNull())
            val factoryBody = factoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val photoPart: MultipartBody.Part? = photoUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile =
                    File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }

                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)
            }

            val response = api.addPlantHead(
                usernameBody,
                emailBody,
                roleBody,
                factoryBody,
                photoPart
            )

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deletePlantHead(id: Int): Result<String> {
        return try {
            val response = api.deletePlantHead(id)
            if (response.isSuccessful) {
                Result.success("User deleted successfully with id: $id")
            } else {
                Result.failure(Exception("Failed to delete user: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteChiefSupervisor(id: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = api.deleteChiefSupervisor(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createProduct(
        name: String,
        imageUri: Uri?,
        quantity: Int,
        categoryId: Int,
        price: Double,
        description: String
    ): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val quantityPart = quantity.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryPart = categoryId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val pricePart = price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val descPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val tempFile =
                    File(context.cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }

                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photo", tempFile.name, requestFile)
            }

            val response = api.createProduct(namePart, imagePart, quantityPart, categoryPart, pricePart, descPart)

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










