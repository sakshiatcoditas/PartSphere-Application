package com.example.partsphere.presentation.registration.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.model.UserRegistrationData
import com.example.partsphere.presentation.registration.repository.DistributorRepository
import com.example.partsphere.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: DistributorRepository
) : ViewModel() {

    // ------------------- User Inputs -------------------
    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var phoneNo by mutableStateOf("")
    var companyName by mutableStateOf("")
    var companyAddress by mutableStateOf("")
    var gstId by mutableStateOf("")
    var state by mutableStateOf("")
    var city by mutableStateOf("")
    var pinCode by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var photoUri by mutableStateOf<Uri?>(null)

    // Field Errors
    var fieldErrors by mutableStateOf<Map<Field, String>>(emptyMap())
        private set

    // Registration State
    var registrationState by mutableStateOf<RegistrationState>(RegistrationState.Idle)
        private set

    // ------------------- Validation -------------------
    fun validatePersonalDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()

        // Full Name: letters and spaces only, cannot be blank
        if (username.isBlank()) {
            errors[Field.FULL_NAME] = "Full name is required"
        } else if (!ValidationUtils.validateName(username)) {
            errors[Field.FULL_NAME] = "Name must contain only letters"
        }

        // Email: regex + valid email format
        if (email.isBlank()) {
            errors[Field.EMAIL] = "Email is required"
        } else if (!ValidationUtils.validateEmail(email)) {
            errors[Field.EMAIL] = "Enter a valid email address"
        }

        // Phone Number: 10 digits, cannot start with 0
        if (phoneNo.isBlank()) {
            errors[Field.PHONE] = "Phone number is required"
        } else if (!ValidationUtils.validatePhone(phoneNo)) {
            errors[Field.PHONE] = "Phone number must be 10 digits"
        }

        fieldErrors = errors
        return errors.isEmpty()
    }

    fun validateCompanyDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()

        // Company Name
        if (companyName.isBlank()) errors[Field.COMPANY_NAME] = "Company name is required"

        // GST ID
        if (gstId.isBlank()) {
            errors[Field.GST_ID] = "GST ID is required"
        } else if (!ValidationUtils.validateGST(gstId)) {
            errors[Field.GST_ID] = "Enter a valid GST ID (e.g. 22AAAAA0000A1Z5)"
        }

        // Company Address
        if (companyAddress.isBlank()) {
            errors[Field.ADDRESS] = "Address is required"
        } else if (companyAddress.length < 10) {
            errors[Field.ADDRESS] = "Address must be at least 10 characters"
        }

        // City
        if (city.isBlank()) errors[Field.CITY] = "City is required"

        // State
        if (state.isBlank()) errors[Field.STATE] = "State is required"

        // PinCode
        if (pinCode.isBlank()) {
            errors[Field.PINCODE] = "Pin code is required"
        } else if (!ValidationUtils.validatePinCode(pinCode)) {
            errors[Field.PINCODE] = "Enter a valid 6-digit pin code"
        }

        fieldErrors = errors
        return errors.isEmpty()
    }

    fun validatePasswordDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()

        // Password
        if (password.isBlank()) {
            errors[Field.PASSWORD] = "Password is required"
        } else if (!ValidationUtils.validatePassword(password)) {
            errors[Field.PASSWORD] = "Password must be 8+ chars, include uppercase, lowercase, number & special char"
        }

        // Confirm Password
        if (confirmPassword.isBlank()) {
            errors[Field.CONFIRM_PASSWORD] = "Confirm password is required"
        } else if (confirmPassword != password) {
            errors[Field.CONFIRM_PASSWORD] = "Passwords do not match"
        }

        fieldErrors = errors
        return errors.isEmpty()
    }

    // ------------------- Registration -------------------
    fun performRegistration(context: Context) {
        if (!validatePersonalDetails() || !validateCompanyDetails() || !validatePasswordDetails()) {
            registrationState = RegistrationState.Idle
            return
        }

        registrationState = RegistrationState.Loading

        val user = UserRegistrationData(
            username = username,
            email = email,
            password = password,
            phoneNo = phoneNo,
            companyName = companyName,
            companyAddress = companyAddress,
            gstId = gstId,
            state = state,
            city = city,
            pinCode = pinCode.toIntOrNull() ?: 0,
            photo = photoUri
        )

        viewModelScope.launch {
            val result = try {
                repository.registerDistributor(context, user)
            } catch (e: Exception) {
                Result.failure<Any>(e)
            }

            registrationState = if (result.isSuccess) {
                RegistrationState.Success
            } else {
                val message = result.exceptionOrNull()?.message ?: "Registration failed"
                RegistrationState.Error(message)
            }
        }
    }
}
