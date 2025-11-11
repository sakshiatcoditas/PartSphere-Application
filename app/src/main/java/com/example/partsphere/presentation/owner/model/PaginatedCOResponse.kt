package com.example.partsphere.presentation.owner.model

data class PaginatedCOResponse(
    val content: List<AddCOResponse>,
    val last: Boolean,
    val totalPages: Int,
    val totalElements: Int,
    val number: Int
)