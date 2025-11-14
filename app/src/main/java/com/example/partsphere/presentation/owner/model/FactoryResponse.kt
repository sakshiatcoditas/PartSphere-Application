package com.example.partsphere.presentation.owner.model

data class FactoryResponse(
    //wrapped the class as the json response was that way
    val content: List<FactoryItem>
)