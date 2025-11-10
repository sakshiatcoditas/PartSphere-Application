package com.example.partsphere.presentation.owner.model


data class PlantHeadResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val photo: String,
    val factory: String? = null // optional

)
