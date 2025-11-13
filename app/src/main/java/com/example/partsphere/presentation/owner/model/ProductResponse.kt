package com.example.partsphere.presentation.owner.model

data class ProductResponse(
    val content: List<Product>,
    val last: Boolean,
    val totalElements: Int,
    val totalPages: Int,
    val size: Int,
    val number: Int,
    val first: Boolean,
    val empty: Boolean
)