package com.example.partsphere.presentation.login_screen.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.presentation.login_screen.DistributorDashboardScreen
import com.example.partsphere.presentation.login_screen.LoginScreen
import com.example.partsphere.presentation.login_screen.HomeScreen
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel

@Composable
fun LoginNavGraph(navController: NavHostController) {
    val loginViewModel: LoginViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = LoginRoute.Login.route
    ) {

        // ------------------- Login Screen -------------------
        composable(LoginRoute.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate(LoginRoute.Registration.route) },
                onNavigateToForgotPassword = { /* Handle forgot password */ },
                onLoginSuccess = { role ->
                    // Navigate to role-specific screens
                    when (role.uppercase()) {
                        "OWNER" -> navController.navigate(LoginRoute.OwnerDashboard.route)
                        "DISTRIBUTOR" -> navController.navigate(LoginRoute.DistributorDashboard.route)
                        else -> navController.navigate(LoginRoute.Home.route)
                    }
                }
            )
        }

        // ------------------- Generic Home Screen -------------------
        composable(LoginRoute.Home.route) {
            HomeScreen()
        }

        // ------------------- Role-based Dashboards (placeholders) -------------------
        composable(LoginRoute.DistributorDashboard.route) {
            DistributorDashboardScreen()
        }

        composable(LoginRoute.OwnerDashboard.route) {
            // TODO: Implement Owner Dashboard screen
        }

        // ------------------- Registration Navigation -------------------
        composable(LoginRoute.Registration.route) {
            // TODO: Link to registration graph if needed
        }
    }
}
