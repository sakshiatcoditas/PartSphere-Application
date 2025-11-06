package com.example.partsphere.presentation.registration.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.presentation.login_screen.LoginScreen
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel
import com.example.partsphere.presentation.registration.registration_screen.*
import com.example.partsphere.presentation.login_screen.HomeScreen
import com.example.partsphere.viewmodel.RegistrationViewModel

@Composable
fun RegistrationNavGraph(navController: NavHostController) {
    val viewModel: RegistrationViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel() // Shared login VM

    NavHost(navController = navController, startDestination = Route.PersonalDetails.route) {

        composable(Route.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = { navController.navigate(Route.PersonalDetails.route) },
                onNavigateToForgotPassword = { /* Handle forgot password */ },
                onLoginSuccess = { navController.navigate(Route.Home.route) {
                    popUpTo(Route.Login.route) { inclusive = true }
                } }
            )
        }

        composable(Route.Home.route) {

            HomeScreen()
        }

        // Example: Registration flow screens
        composable(Route.PersonalDetails.route) {
            RegisterScreen(
                viewModel = viewModel,
                onProceedClick = { if (viewModel.validatePersonalDetails()) navController.navigate(Route.CompanyDetails.route) },
                onLoginClick = { navController.navigate(Route.Login.route) }
            )
        }

        composable(Route.CompanyDetails.route) {
            CompanyDetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProceedClick = { if (viewModel.validateCompanyDetails()) navController.navigate(Route.SetPassword.route) }
            )
        }

        composable(Route.SetPassword.route) {
            SetPasswordScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Route.Success.route) { popUpTo(Route.PersonalDetails.route) { inclusive = true } } }
            )
        }

        composable(Route.Success.route) {
            SuccessScreen(
                onBackToLoginClick = { navController.navigate(Route.Login.route) { popUpTo(Route.PersonalDetails.route) { inclusive = true } } }
            )
        }
    }
}
