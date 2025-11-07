package com.example.partsphere.presentation.owner.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.presentation.owner.model.FactoryItem
import com.example.partsphere.presentation.owner.repository.OwnerRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun fetchFactories(page: Int = 0, size: Int = 5) {
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
}
