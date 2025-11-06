package com.example.partsphere.utils

import android.util.Patterns


object ValidationUtils {
    val NAME_REGEX = Regex("^[A-Za-z ]+$")
    val PHONE_REGEX = Regex("^[0-9]{10}$")
    //val GST_REGEX = Regex("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")
    val PINCODE_REGEX = Regex("^[1-9][0-9]{5}$")
    val PASSWORD_REGEX = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")

    fun validateName(name: String) = name.isNotBlank() && NAME_REGEX.matches(name)
    fun validatePhone(phone: String) = PHONE_REGEX.matches(phone)
    fun validateEmail(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()
    fun validateGST(gst: String) = true
    fun validatePinCode(pin: String) = PINCODE_REGEX.matches(pin)
    fun validatePassword(password: String) = PASSWORD_REGEX.matches(password)
}
