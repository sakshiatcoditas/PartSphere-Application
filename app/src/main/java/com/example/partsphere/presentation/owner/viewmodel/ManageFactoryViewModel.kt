package com.example.partsphere.presentation.owner.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.presentation.owner.model.AddChiefSupervisorResponse
import com.example.partsphere.presentation.owner.model.CentralOfficerUiState
import com.example.partsphere.presentation.owner.model.FactoryItem
import com.example.partsphere.presentation.owner.model.PlantHeadResponse
import com.example.partsphere.presentation.owner.model.ProductUiState
import com.example.partsphere.presentation.owner.model.SupervisorFactory
import com.example.partsphere.presentation.owner.model.UnassignedPlantHead
import com.example.partsphere.presentation.owner.model.UpdateFactoryRequest
import com.example.partsphere.presentation.owner.repository.OwnerRepository
import com.example.partsphere.presentation.owner.ui.manageemployee.CentralOfficer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageFactoryViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    private val _factories = MutableStateFlow<List<FactoryItem>>(emptyList())
    val factories: StateFlow<List<FactoryItem>> = _factories

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    init {
        fetchFactories()
    }

    fun fetchFactories(page: Int = 0, size: Int = 100) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getFactories(page, size)
                _factories.value = response?.content ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _unassignedPlantHeads = MutableStateFlow<List<UnassignedPlantHead>>(emptyList())
    val unassignedPlantHeads: StateFlow<List<UnassignedPlantHead>> = _unassignedPlantHeads

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> = _updateMessage

    fun fetchUnassignedPlantHeads() {
        viewModelScope.launch {
            val response = repository.getUnassignedPlantHeads()
            if (response.isSuccessful) {
                val list = response.body().orEmpty()
                Log.d("API_DEBUG", "Fetched unassigned plant heads: $list")
                _unassignedPlantHeads.value = list
            } else {
                Log.e("API_DEBUG", "Error: ${response.code()} - ${response.message()}")
            }
        }
    }


    fun updateFactory(factoryId: Int, request: UpdateFactoryRequest) {
        viewModelScope.launch {
            val response = repository.updateFactory(factoryId, request)
            if (response.isSuccessful) {
                _updateMessage.value = response.body()?.get("message")
            } else {
                _updateMessage.value = "Update failed"
            }
        }
    }

    fun clearUpdateMessage() {
        _updateMessage.value = null
    }


//    fun updateFactory(factoryId: Int, newName: String, onResult: (Boolean) -> Unit) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null
//            try {
//                val success = repository.updateFactory(factoryId, newName)
//                if (success) {
//                    // Update local list immediately
//                    _factories.value = _factories.value.map {
//                        if (it.id == factoryId) it.copy(name = newName) else it
//                    }
//                }
//                onResult(success)
//            } catch (e: Exception) {
//                _errorMessage.value = e.localizedMessage ?: "Unknown error"
//                onResult(false)
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    private val _isCreating = MutableStateFlow(false)
    val isCreating = _isCreating.asStateFlow()

    private val _createMessage = MutableStateFlow<String?>(null)
    val createMessage = _createMessage.asStateFlow()

    fun createFactory(
        name: String,
        location: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isCreating.value = true
            val result = repository.createFactory(name, location)
            result.onSuccess { response ->
                _createMessage.value = response.message
                fetchFactories() // optional: refresh factory list
                onResult(true, response.message)
            }.onFailure { e ->
                val errorMsg = e.message ?: "Failed to create factory"
                _createMessage.value = errorMsg
                onResult(false, errorMsg)
            }
            _isCreating.value = false
        }
    }

    fun deleteFactory(factoryId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.deleteFactory(factoryId)
                result.onSuccess { message ->
                    //  Remove deleted factory from list immediately
                    _factories.value = _factories.value.filterNot { it.id == factoryId }
                    onResult(true, message)
                }.onFailure { e ->
                    onResult(false, e.message ?: "Failed to delete factory")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    //Add CO Officer

    private val _uiState = MutableStateFlow(CentralOfficerUiState())
    val uiState: StateFlow<CentralOfficerUiState> = _uiState

    fun addCentralOfficer(name: String, email: String, photoUri: Uri?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = repository.addCentralOfficer(name, email, photoUri)

            result.onSuccess { coResponse ->
                val co = CentralOfficer(
                    id = coResponse.id,
                    name = coResponse.username,
                    email = coResponse.email,
                    photoUrl = coResponse.photo // Cloudinary URL from backend
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    officers = _uiState.value.officers + co
                )

                // Refresh the list
                fetchCentralOfficers()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun fetchCentralOfficers(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (!loadMore) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }

            val result = repository.getAllCentralOfficers(loadAll = !loadMore)

            result.onSuccess { coList ->
                if (coList.isNotEmpty()) {
                    val officers = coList.map { response ->
                        CentralOfficer(
                            id = response.id,
                            name = response.username,
                            email = response.email,
                            photoUrl = response.photo?.replace("http://", "https://")
                        )
                    }

                    val updatedList = if (loadMore)
                        _uiState.value.officers + officers.distinctBy { it.id }
                    else
                        officers.distinctBy { it.id }

                    _uiState.value = _uiState.value.copy(isLoading = false, officers = updatedList)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }


    private val _wasDeleted = MutableStateFlow(false)
    val wasDeleted: StateFlow<Boolean> = _wasDeleted


    fun deleteCentralOfficer(officerId: Int) {
        viewModelScope.launch {
            val currentList = _uiState.value.officers
            val updatedList = currentList.filterNot { it.id == officerId }
            _uiState.value = _uiState.value.copy(officers = updatedList)

            val result = repository.deleteCentralOfficer(officerId)

            result.onSuccess {
                _wasDeleted.value = true  // set flag
                fetchCentralOfficers()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateCentralOfficer(
        context: Context, // add context here
        officerId: Int,
        username: String,
        email: String,
        photoUri: Uri?,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            val result = repository.updateCentralOfficer(
                context = context,
                officerId = officerId,
                username = username,
                email = email,
                photoUri = photoUri
            )

            if (result.isSuccess) {
                fetchCentralOfficers() // refresh list after update
                onResult(true)
            } else {
                _uiState.update {
                    it.copy(error = result.exceptionOrNull()?.message)
                }
                onResult(false)
            }

            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }


    var plantHeads by mutableStateOf<List<PlantHeadResponse>>(emptyList())
        private set

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun fetchPlantHeads() {
        viewModelScope.launch {
            try {
                loading = true
                val data = repository.getPlantHeads()
                plantHeads = data ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                error = e.message
            } finally {
                loading = false
            }
        }
    }


//    fun deletePlantHead(plantHeadId: Int, onResult: (Boolean, String) -> Unit) {
//        viewModelScope.launch {
//            loading = true
//            try {
//                val result = repository.deletePlantHead(plantHeadId)
//                result.onSuccess { message ->
//                    // Remove deleted PlantHead from list
//                    plantHeads = plantHeads.filterNot { it.id == plantHeadId }
//                    onResult(true, message)
//                }.onFailure { e ->
//                    onResult(false, e.message ?: "Failed to delete Plant Head")
//                }
//            } finally {
//                loading = false
//            }
//        }
//    }


//  Chief Supervisor Pagination Handling


    data class ChiefSupervisorUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val supervisors: List<AddChiefSupervisorResponse> = emptyList()
    )

    private val _chiefUiState = MutableStateFlow(ChiefSupervisorUiState())
    val chiefUiState: StateFlow<ChiefSupervisorUiState> = _chiefUiState

    private val _supervisorFactories = MutableStateFlow<List<SupervisorFactory>>(emptyList())
    val supervisorFactories: StateFlow<List<SupervisorFactory>> = _supervisorFactories

    private val _isAddingSupervisor = MutableStateFlow(false)
    val isAddingSupervisor = _isAddingSupervisor.asStateFlow()

    fun fetchChiefSupervisors(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (!loadMore) {
                _chiefUiState.value = _chiefUiState.value.copy(isLoading = true, error = null)
            }

            val result = repository.getAllChiefSupervisors(loadAll = !loadMore)

            result.onSuccess { newList ->
                if (newList.isNotEmpty()) {
                    val updatedList = if (loadMore)
                        _chiefUiState.value.supervisors + newList.distinctBy { it.id }
                    else
                        newList.distinctBy { it.id }

                    _chiefUiState.value = _chiefUiState.value.copy(
                        isLoading = false,
                        supervisors = updatedList
                    )
                } else {
                    _chiefUiState.value = _chiefUiState.value.copy(isLoading = false)
                }
            }.onFailure { e ->
                _chiefUiState.value = _chiefUiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun fetchFactoriesForSupervisor() {
        viewModelScope.launch {
            val result = repository.getFactoriesForSupervisor()
            result.onSuccess { factories ->
                _supervisorFactories.value = factories
            }.onFailure { e ->
                // Optional: handle error
                _supervisorFactories.value = emptyList()
            }
        }
    }

    // ------------------ ADD CHIEF SUPERVISOR ------------------
    fun addChiefSupervisor(
        name: String,
        email: String,
        factoryId: Long,
        factoryName: String, // Add this parameter
        photoUri: Uri?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isAddingSupervisor.value = true
            val result = repository.addChiefSupervisor(name, email, factoryId, photoUri)

            result.onSuccess { supervisor ->
                // Inject factoryName into the supervisor object
                val supervisorWithFactory = supervisor.copy(factoryName = factoryName)
                _chiefUiState.update {
                    it.copy(supervisors = it.supervisors + supervisorWithFactory)
                }
                onResult(true, "Supervisor added successfully")
            }.onFailure { e ->
                onResult(false, e.message ?: "Failed to add supervisor")
            }
            _isAddingSupervisor.value = false
        }
    }


    private val _productUiState = MutableStateFlow(ProductUiState())
    val productUiState: StateFlow<ProductUiState> = _productUiState

    fun fetchProducts(page: Int = 0, size: Int = 5, isNextPage: Boolean = false) {
        viewModelScope.launch {
            _productUiState.update { it.copy(isLoading = true, error = null) }

            val result = repository.getAllProducts(page, size)
            result.onSuccess { response ->
                _productUiState.update {
                    it.copy(
                        products = if (isNextPage) it.products + response.content else response.content,
                        currentPage = response.number,
                        totalPages = response.totalPages,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure { e ->
                _productUiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to load products")
                }
            }
        }
    }

    fun loadNextPage() {
        val state = _productUiState.value
        if (!state.isLoading && state.currentPage + 1 < state.totalPages) {
            fetchProducts(page = state.currentPage + 1, isNextPage = true)
        }
    }



}














