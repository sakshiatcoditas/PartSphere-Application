package com.example.partsphere.model

data class DistributorRegistrationRequest(
    val username: String,
    val email: String,
    val phoneNo: String,
    val companyName: String,
    val gstId: String? = null,      // optional
    val companyAddress: String,
    val city: String,
    val photo: String? = null,      // can be null
    val state: String,
    val pinCode: Int,
    val password: String
)
