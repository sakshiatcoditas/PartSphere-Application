package com.example.partsphere.presentation.owner.model

data class ProductUiState(
    val products: List<Product> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val deletingProductIds: Set<Int> = emptySet() // 🔹 track deleting

)