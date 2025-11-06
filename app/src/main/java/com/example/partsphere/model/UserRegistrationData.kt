package com.example.partsphere.model

import android.net.Uri


data class UserRegistrationData(
    var username: String = "",         // was fullName
    var email: String = "",
    var password: String = "",
    var phoneNo: String = "",          // was phoneNumber
    var companyName: String = "",
    var companyAddress: String = "",   // was address
    var gstId: String = "",
    var state: String = "",
    var city: String = "",
    var pinCode: Int = 0,              // was String, backend expects int
    var photo: Uri? = null             // was photoUrl
)

