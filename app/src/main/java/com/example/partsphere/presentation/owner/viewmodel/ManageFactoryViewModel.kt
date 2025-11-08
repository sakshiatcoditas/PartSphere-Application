package com.example.partsphere.presentation.owner.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.presentation.owner.model.AddCOResponse
import com.example.partsphere.presentation.owner.model.CentralOfficerUiState
import com.example.partsphere.presentation.owner.model.FactoryItem
import com.example.partsphere.presentation.owner.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun updateFactory(factoryId: Int, newName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val success = repository.updateFactory(factoryId, newName)
                if (success) {
                    // Update local list immediately
                    _factories.value = _factories.value.map {
                        if (it.id == factoryId) it.copy(name = newName) else it
                    }
                }
                onResult(success)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

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
            result.onSuccess { co ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    officers = _uiState.value.officers + co
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }




}