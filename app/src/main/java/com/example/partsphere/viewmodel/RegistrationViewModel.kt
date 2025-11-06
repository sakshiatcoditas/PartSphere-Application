package com.example.partsphere.viewmodel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.partsphere.utils.*

class RegistrationViewModel : ViewModel() {

    // ------------------- User Inputs -------------------
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var companyName by mutableStateOf("")
    var gstId by mutableStateOf("")
    var address by mutableStateOf("")
    var city by mutableStateOf("")
    var state by mutableStateOf("")
    var pincode by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var avatarUri by mutableStateOf<Uri?>(null) // optional

    //  Field Errors
    var fieldErrors by mutableStateOf<Map<Field, String>>(emptyMap())
        private set

    //  Registration State
    var registrationState by mutableStateOf<RegistrationState>(RegistrationState.Idle)
        private set

    //  Validation

    // Validate only Personal Details (RegisterScreen)
    fun validatePersonalDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()

        if (!ValidationUtils.validateName(fullName)) errors[Field.FULL_NAME] = "Enter a valid name"
        if (!ValidationUtils.validateEmail(email)) errors[Field.EMAIL] = "Enter a valid email"
        if (!ValidationUtils.validatePhone(phoneNumber)) errors[Field.PHONE] = "Enter a valid phone number"

        fieldErrors = errors
        return errors.isEmpty()
    }

    // Validate only Company Details (CompanyDetailsScreen)
    fun validateCompanyDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()

        if (companyName.isBlank()) errors[Field.COMPANY_NAME] = "Company name required"
        if (!ValidationUtils.validateGST(gstId)) errors[Field.GST_ID] = "Invalid GST ID"
        if (address.length < 10) errors[Field.ADDRESS] = "Address too short"
        if (city.isBlank()) errors[Field.CITY] = "City required"
        if (state.isBlank()) errors[Field.STATE] = "State required"
        if (!ValidationUtils.validatePinCode(pincode)) errors[Field.PINCODE] = "Invalid pincode"

        fieldErrors = errors
        return errors.isEmpty()
    }

    // Validate only Password Details (SetPasswordScreen)
    fun validatePasswordDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()

        if (!ValidationUtils.validatePassword(password)) errors[Field.PASSWORD] =
            "Password must be 8+ chars, include uppercase, lowercase, number & special char"
        if (confirmPassword != password) errors[Field.CONFIRM_PASSWORD] = "Passwords do not match"

        fieldErrors = errors
        return errors.isEmpty()
    }

    // Optional: final submission (all screens validated)
    fun submitRegistration() {
        if (validatePersonalDetails() && validateCompanyDetails() && validatePasswordDetails()) {
            registrationState = RegistrationState.Success
        } else {
            registrationState = RegistrationState.Idle
        }
    }
}
