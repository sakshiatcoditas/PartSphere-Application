package com.example.partsphere.presentation.owner.model

data class UnassignedFactoryResponse(
    val data: List<UnassignedFactoryItem>,
    val count: Int,
    val filterType: String,
    val status: String
)