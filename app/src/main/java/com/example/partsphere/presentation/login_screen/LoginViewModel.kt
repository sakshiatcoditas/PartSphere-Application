package com.example.partsphere.presentation.login_screen



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

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

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val body = response.body()

                    if (body != null && body.token != null) {
                        prefs.saveToken(body.token) // Save token
                        _authState.value = AuthState.Success(body.message ?: "Login successful")
                    }

                    else {
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
