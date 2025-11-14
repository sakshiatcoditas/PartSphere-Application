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
import com.example.partsphere.presentation.owner.ui.OwnerMainScreen

@Composable
fun LoginNavGraph(rootNavController: NavHostController) {

    val loginViewModel: LoginViewModel = hiltViewModel()
    val prefs = loginViewModel.getPrefs()

    NavHost(
        navController = rootNavController,
        startDestination = LoginRoute.Login.route
    ) {

        composable(LoginRoute.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    rootNavController.navigate(LoginRoute.Registration.route)
                },
                onNavigateToForgotPassword = { },

                onLoginSuccess = { role ->
                    when (role.uppercase()) {

                        "OWNER" -> {
                            rootNavController.navigate("owner_graph") {
                                popUpTo(0) { inclusive = true }
                            }
                        }

                        "DISTRIBUTOR" -> {
                            rootNavController.navigate("distributor_graph") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        composable(LoginRoute.Registration.route) {
            // TODO
        }
    }
}
