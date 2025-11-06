package com.example.partsphere.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.model.UserRegistrationData
import com.example.partsphere.repository.DistributorRepository
import com.example.partsphere.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject  // Make sure this import is present



@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: DistributorRepository
) : ViewModel() {


    // ------------------- User Inputs -------------------
    var username by mutableStateOf("")       // was fullName
    var email by mutableStateOf("")
    var phoneNo by mutableStateOf("")        // was phoneNumber
    var companyName by mutableStateOf("")
    var companyAddress by mutableStateOf("") // was address
    var gstId by mutableStateOf("")
    var state by mutableStateOf("")
    var city by mutableStateOf("")
    var pinCode by mutableStateOf("")        // still input as String, convert later
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var photoUri by mutableStateOf<Uri?>(null) // optional

    // Field Errors
    var fieldErrors by mutableStateOf<Map<Field, String>>(emptyMap())
        private set

    // Registration State
    var registrationState by mutableStateOf<RegistrationState>(RegistrationState.Idle)
        private set

    // ------------------- Validation -------------------
    fun validatePersonalDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()
        if (!ValidationUtils.validateName(username)) errors[Field.FULL_NAME] = "Enter a valid name"
        if (!ValidationUtils.validateEmail(email)) errors[Field.EMAIL] = "Enter a valid email"
        if (!ValidationUtils.validatePhone(phoneNo)) errors[Field.PHONE] = "Enter a valid phone number"
        fieldErrors = errors
        return errors.isEmpty()
    }

    fun validateCompanyDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()
        if (companyName.isBlank()) errors[Field.COMPANY_NAME] = "Company name required"
        if (!ValidationUtils.validateGST(gstId)) errors[Field.GST_ID] = "Invalid GST ID"
        if (companyAddress.isBlank() || companyAddress.length < 10) errors[Field.ADDRESS] = "Address too short"
        if (city.isBlank()) errors[Field.CITY] = "City required"
        if (state.isBlank()) errors[Field.STATE] = "State required"
        if (!ValidationUtils.validatePinCode(pinCode)) errors[Field.PINCODE] = "Invalid pincode"
        fieldErrors = errors
        return errors.isEmpty()
    }

    fun validatePasswordDetails(): Boolean {
        val errors = mutableMapOf<Field, String>()
        if (!ValidationUtils.validatePassword(password)) errors[Field.PASSWORD] = "Password must be 8+ chars, include uppercase, lowercase, number & special char"
        if (confirmPassword != password) errors[Field.CONFIRM_PASSWORD] = "Passwords do not match"
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

        // Build UserRegistrationData matching backend DTO
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
            pinCode = pinCode.toIntOrNull() ?: 0, // convert to Int
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
