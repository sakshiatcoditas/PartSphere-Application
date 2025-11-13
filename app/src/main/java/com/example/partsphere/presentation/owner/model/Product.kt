package com.example.partsphere.presentation.owner.model

data class Product(
    val id: Int,
    val name: String,
    val categoryName: String,
    val description: String,
    val price: Double,
    val imageUrl: String?
)