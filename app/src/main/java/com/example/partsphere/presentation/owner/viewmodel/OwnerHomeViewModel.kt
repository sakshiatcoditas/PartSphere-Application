package com.example.partsphere.presentation.owner.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.presentation.owner.model.EmployeeCount
import com.example.partsphere.presentation.owner.model.FactoryLocation
import com.example.partsphere.presentation.owner.repository.OwnerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val repository: OwnerRepository
) : ViewModel() {

    var employeeCounts by mutableStateOf<List<EmployeeCount>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchEmployeeCounts()
    }

    fun fetchEmployeeCounts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                employeeCounts = repository.getEmployeeCounts()
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }



    // --------------- Factory Location wise count----------------

    var factoryLocations = mutableStateOf<List<FactoryLocation>>(emptyList())
        private set

    var isFactoryLoading = mutableStateOf(false)
        private set

    var factoryErrorMessage = mutableStateOf<String?>(null)
        private set

    fun fetchFactoryLocations() {
        viewModelScope.launch {
            isFactoryLoading.value = true
            factoryErrorMessage.value = null

            val result = repository.getFactoryCountByLocation()
            result.onSuccess {
                factoryLocations.value = it
            }.onFailure {
                factoryErrorMessage.value = it.message
            }

            isFactoryLoading.value = false
        }
    }




}
