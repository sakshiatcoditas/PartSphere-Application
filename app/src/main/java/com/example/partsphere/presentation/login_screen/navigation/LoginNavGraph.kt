package com.example.partsphere.presentation.login_screen.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.partsphere.presentation.login_screen.LoginScreen
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel
import androidx.navigation.NavHostController

@Composable
fun LoginNavGraph(
    rootNavController: NavHostController
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    // Just call LoginScreen directly
    LoginScreen(
        viewModel = loginViewModel,
        onNavigateToRegister = { rootNavController.navigate("registration_graph") },
        onNavigateToForgotPassword = { /* TODO */ },
        onLoginSuccess = { role ->
            when (role.uppercase()) {
                "OWNER" -> rootNavController.navigate("owner_graph") {
                    popUpTo(0) { inclusive = true }
                }
                "DISTRIBUTOR" -> rootNavController.navigate("distributor_graph") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    )
}