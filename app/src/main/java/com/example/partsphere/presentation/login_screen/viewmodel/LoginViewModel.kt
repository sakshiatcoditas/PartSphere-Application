package com.example.partsphere.presentation.login_screen.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.presentation.login_screen.AuthState
import com.example.partsphere.presentation.login_screen.repository.LoginRepository
import com.example.partsphere.network.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val prefs: PreferenceManager
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun getPrefs(): PreferenceManager = prefs

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState


    var fieldErrors by mutableStateOf<Map<String, String>>(emptyMap())
        private set

    fun validateInputs(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (email.isBlank()) errors["email"] = "Email is required"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            errors["email"] = "Enter a valid email"

        if (password.isBlank()) errors["password"] = "Password is required"
        else if (password.length < 6) errors["password"] = "Password must be at least 6 characters"

        fieldErrors = errors
        return errors.isEmpty()
    }

    fun login(email: String, password: String) {
        this.email = email
        this.password = password

        if (!validateInputs()) return

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null && body.token != null) {
                        prefs.saveToken(body.token)
                        prefs.saveRole(body.role ?: "")
                        _authState.value = AuthState.Success(
                            body.message ?: "Login successful",
                            role = body.role
                        )
                    } else {
                        _authState.value = AuthState.Error(body?.error ?: "Unknown error")
                    }
                } else {
                    _authState.value = AuthState.Error("Wrong username or password")
                }
            } catch (e: IOException) {
                _authState.value = AuthState.Error("Network Error")
            } catch (e: HttpException) {
                _authState.value = AuthState.Error("Server Error")
            }
        }
    }


}
