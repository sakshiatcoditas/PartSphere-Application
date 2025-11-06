package com.example.partsphere.model



data class UserRegistrationData(
    var photoUrl: String? = null,       // optional
    var fullName: String = "",
    var email: String = "",
    var phoneNumber: String = "",
    var companyName: String = "",
    var gstId: String = "",
    var address: String = "",
    var city: String = "",
    var state: String = "",
    var pincode: String = "",
    var password: String = "",
    var confirmPassword: String = ""
)
