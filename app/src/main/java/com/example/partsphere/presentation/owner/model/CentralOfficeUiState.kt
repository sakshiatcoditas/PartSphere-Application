package com.example.partsphere.presentation.owner.model

data class CentralOfficerUiState(
    val isLoading: Boolean = false,
    val officers: List<AddCOResponse> = emptyList(),
    val error: String? = null
)