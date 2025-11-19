package com.example.partsphere.presentation.owner.model

data class PlantHeadPageable(
    val pageNumber: Int,
    val pageSize: Int,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)
