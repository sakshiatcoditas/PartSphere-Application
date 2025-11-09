package com.example.partsphere.presentation.owner.model

import com.example.partsphere.presentation.owner.ui.CentralOfficer

data class CentralOfficerUiState(
    val isLoading: Boolean = false,
    val officers: List<CentralOfficer> = emptyList(),
    val error: String? = null
)