package com.example.partsphere.presentation.owner.model

data class AddPlantHeadResponse(
    val success: Boolean,
    val message: String,
    val data: PlantHeadResponse?
)
