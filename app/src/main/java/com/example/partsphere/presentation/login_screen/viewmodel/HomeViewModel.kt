package com.example.partsphere.presentation.login_screen.viewmodel

import androidx.lifecycle.ViewModel
import com.example.partsphere.presentation.owner.data.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefs: PreferenceManager
): ViewModel() {
    val token get() = prefs.getToken() ?: "No token found"
}