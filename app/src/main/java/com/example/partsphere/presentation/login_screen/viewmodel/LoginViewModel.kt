package com.example.partsphere.presentation.login_screen.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.partsphere.R
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
    private val prefs: PreferenceManager,
    private val context: Context
) : ViewModel() {
    fun getPrefs(): PreferenceManager = prefs

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
                        prefs.saveToken(body.token)
                        prefs.saveRole(body.role ?: "")
                        _authState.value = AuthState.Success(
                            message = context.getString(R.string.login_successful),
                            role = body.role
                        )
                    }


                    else {
                        _authState.value = AuthState.Error(
                            message = body?.error ?: context.getString(R.string.unknown_error)
                        )
                    }
                } else {
                    _authState.value = AuthState.Error(
                        message = context.getString(R.string.wrong_username_or_password)
                    )
                }
            } catch (e: IOException) {
                _authState.value = AuthState.Error(
                    message = context.getString(R.string.network_error)
                )
            } catch (e: HttpException) {
                _authState.value = AuthState.Error(
                    message = context.getString(R.string.server_error)
                )
            }
        }
    }
}