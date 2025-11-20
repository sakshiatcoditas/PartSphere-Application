package com.example.partsphere.presentation.owner.model

data class AddCOResponse(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    val photo: String?=null
)