package com.example.partsphere.network

import com.example.partsphere.model.DistributorRegistrationResponse
import com.example.partsphere.presentation.login_screen.model.LoginRequest
import com.example.partsphere.presentation.login_screen.model.LoginResponse
import com.example.partsphere.presentation.owner.model.AddCOResponse
import com.example.partsphere.presentation.owner.model.AddChiefSupervisorResponse
import com.example.partsphere.presentation.owner.model.ChiefSupervisorPaginatedResponse
import com.example.partsphere.presentation.owner.model.CreateFactoryRequest
import com.example.partsphere.presentation.owner.model.CreateFactoryResponse
import com.example.partsphere.presentation.owner.model.DeleteFactoryResponse
import com.example.partsphere.presentation.owner.model.DeletePlantHeadResponse
import com.example.partsphere.presentation.owner.model.DeleteProductResponse
import com.example.partsphere.presentation.owner.model.DeleteResponse
import com.example.partsphere.presentation.owner.model.EmployeeCountResponse
import com.example.partsphere.presentation.owner.model.FactoryDropdownResponse
import com.example.partsphere.presentation.owner.model.FactoryLocationResponse
import com.example.partsphere.presentation.owner.model.FactoryResponse
import com.example.partsphere.presentation.owner.model.PaginatedCOResponse
import com.example.partsphere.presentation.owner.model.PlantHeadPaginatedResponse
import com.example.partsphere.presentation.owner.model.ProductResponse
import com.example.partsphere.presentation.owner.model.UnassignedPlantHead
import com.example.partsphere.presentation.owner.model.UpdateFactoryRequest
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
    @POST("/auth/signup")
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

    @POST("/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>


    //API to get the employee count comparision
    @GET("/api/users/count")
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
//    @PATCH("/api/factory/update/{id}")
//    suspend fun updateFactory(
//        @Path("id") id: Int,
//        @Body body: Map<String, String>
//    ): Response<Unit>

    @PATCH("/api/factory/update/{id}")
    suspend fun updateFactory(
        @Path("id") factoryId: Int,
        @Body request: UpdateFactoryRequest
    ): Response<Map<String, String>>



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

    @Multipart
    @POST("/api/users/centralofficer")
    suspend fun addCentralOfficer(
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<AddCOResponse>




    @GET("/api/users/role/central-officer")
    suspend fun getAllCentralOfficers(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PaginatedCOResponse>



    @DELETE("api/users/delete/{id}")
    suspend fun deleteCentralOfficer(@Path("id") id: Int): Response<DeleteResponse>


    @Multipart
    @PATCH("/api/users/update-emp")
    suspend fun updateCentralOfficer(
        @Part("id") id: RequestBody,
        @Part("username") username: RequestBody,
        @Part("email") email: RequestBody,
        @Part photo: MultipartBody.Part? // optional
    ): Response<AddCOResponse>



    //  GET unassigned plant heads
       @GET("/api/users/unassigned-planthead")
       suspend fun getUnassignedPlantHeads(): Response<List<UnassignedPlantHead>>

//------------ Plant Head -----------------------------------------

    @GET("api/users/role/plant-head")
    suspend fun getPlantHeads(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<PlantHeadPaginatedResponse>





    @DELETE("api/users/delete/{id}")
        suspend fun deleteEmployee(@Path("id") id: Int): Response<DeletePlantHeadResponse>


    //Get All ChiefSupervisor

    @GET("/api/users/role/chief-supervisor")
    suspend fun getAllChiefSupervisors(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ChiefSupervisorPaginatedResponse>

    @Multipart
    @POST("/api/users/ph-supervisor")
    suspend fun addChiefSupervisor(
        @Part("username") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("fcatory_id") factoryId: RequestBody,
        @Part("role") role: RequestBody,

        @Part photo: MultipartBody.Part?
    ): Response<AddChiefSupervisorResponse>

    @GET("/api/factory")
    suspend fun getAllFactoriesForSupervisor(
        @Query("type") type: String = "all"
    ): Response<FactoryDropdownResponse>


    //get all products

    @GET("/api/product/all")
    suspend fun getAllProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 5
    ): Response<ProductResponse>

    @DELETE("/api/product/delete/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<DeleteProductResponse>



}
