package com.example.partsphere.presentation.owner.model

data class ChiefSupervisorPaginatedResponse(
    val content: List<AddChiefSupervisorResponse>,
    val number: Int,
    val last: Boolean
)