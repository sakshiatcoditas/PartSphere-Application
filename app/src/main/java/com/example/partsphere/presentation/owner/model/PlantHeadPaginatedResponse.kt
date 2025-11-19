package com.example.partsphere.presentation.owner.model


data class PlantHeadPaginatedResponse(
    val content: List<PlantHeadResponse>,
    val pageable: PlantHeadPageable,
    val last: Boolean,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int
)


