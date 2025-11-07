package com.example.partsphere.presentation.owner.model

data class EmployeeCountResponse(
    val data: List<EmployeeCountDto>,
    val status: String
)