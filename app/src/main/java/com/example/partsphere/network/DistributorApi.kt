package com.example.partsphere.network

import com.example.partsphere.model.DistributorRegistrationResponse
import com.example.partsphere.presentation.login_screen.model.LoginRequest
import com.example.partsphere.presentation.login_screen.model.LoginResponse
import com.example.partsphere.presentation.owner.model.CreateFactoryRequest
import com.example.partsphere.presentation.owner.model.CreateFactoryResponse
import com.example.partsphere.presentation.owner.model.DeleteFactoryResponse
import com.example.partsphere.presentation.owner.model.EmployeeCountResponse
import com.example.partsphere.presentation.owner.model.FactoryLocationResponse
import com.example.partsphere.presentation.owner.model.FactoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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


    //API to get the employee count comparision
    @GET(" /api/users/count")
    suspend fun getEmployeeCounts(): Response<EmployeeCountResponse>

    //API to get the location wise count of the factories
    @GET("/api/factory/location-count")
    suspend fun getFactoryCountByLocation(): Response<FactoryLocationResponse>

    //GET all Factories
    @GET("/api/factory/all")
    suspend fun getAllFactories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5
    ): Response<FactoryResponse>


    //Update Factory
    @PATCH("/api/factory/update/{id}")
    suspend fun updateFactory(
        @Path("id") id: Int,
        @Body body: Map<String, String>
    ): Response<Unit>


    //Create New Factory
    @POST("/api/factory/newfactory")
    suspend fun createFactory(
        @Body request: CreateFactoryRequest
    ): Response<CreateFactoryResponse>


    //Delete A factory
    @DELETE("api/factory/{id}")
    suspend fun deleteFactory(
        @Path("id") factoryId: Int
    ): Response<DeleteFactoryResponse>


}
