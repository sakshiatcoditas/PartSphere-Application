package com.example.partsphere.presentation.login_screen.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.partsphere.presentation.login_screen.LoginScreen
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel

@Composable
fun LoginNavGraph(
    navController: NavHostController
) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    // The NavHost is already managed by RootNavGraph
    LoginScreen(
        viewModel = loginViewModel,
        onNavigateToRegister = {
            // Navigate to registration route in the RootNavGraph
            navController.navigate(LoginRoute.Registration.route) {
                launchSingleTop = true
            }
        },
        onNavigateToForgotPassword = { /* TODO */ },
        onLoginSuccess = { role ->
            when (role.uppercase()) {
                "OWNER" -> navController.navigate("owner_graph") {
                    popUpTo(0) { inclusive = true }
                }
                "DISTRIBUTOR" -> navController.navigate("distributor_graph") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    )
}