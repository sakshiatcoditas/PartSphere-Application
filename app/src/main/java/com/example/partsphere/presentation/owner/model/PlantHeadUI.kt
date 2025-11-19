package com.example.partsphere.presentation.owner.model

import android.net.Uri

data class PlantHeadUI(
    val id: Int,
    val name: String,
    val email: String,
    val designation: String,
    val factory: String,
    val photoUri: Uri?
)