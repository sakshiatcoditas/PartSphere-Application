package com.example.partsphere.presentation.owner.model

data class AddChiefSupervisorResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val photo: String?,
    val factoryName: String
)