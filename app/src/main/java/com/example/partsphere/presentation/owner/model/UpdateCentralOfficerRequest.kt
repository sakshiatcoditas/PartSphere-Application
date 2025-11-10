package com.example.partsphere.presentation.owner.model

data class UpdateCentralOfficerRequest(
    val id: Int,
    val username: String,
    val email: String,
    val photo: String? = ""
)