package com.example.partsphere.presentation.owner.model

data class AddPlantHeadRequest(
    val username: String,
    val email: String,
    val role: String,
    val factory_id: Int
)