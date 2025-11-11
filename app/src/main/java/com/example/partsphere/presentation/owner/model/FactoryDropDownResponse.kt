package com.example.partsphere.presentation.owner.model

data class FactoryDropdownResponse(
    val data: List<SupervisorFactory>,
    val count: Int,
    val filterType: String,
    val status: String
)