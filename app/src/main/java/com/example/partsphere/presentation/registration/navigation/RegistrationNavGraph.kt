package com.example.partsphere.presentation.registration.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.registration.registration_screen.*
import com.example.partsphere.presentation.registration.viewmodel.RegistrationViewModel

@Composable
fun RegistrationNavGraph(rootNavController: NavHostController) {
    val viewModel: RegistrationViewModel = hiltViewModel()
    val registrationNavController = rememberNavController()

    NavHost(
        navController = registrationNavController,
        startDestination = Route.PersonalDetails.route
    ) {
        composable(Route.PersonalDetails.route) {
            RegisterScreen(
                viewModel = viewModel,
                onProceedClick = {
                    if (viewModel.validatePersonalDetails()) {
                        registrationNavController.navigate(Route.CompanyDetails.route)
                    }
                },
                onLoginClick = {
                    rootNavController.navigate(LoginRoute.Login.route) {
                        popUpTo(LoginRoute.Registration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.CompanyDetails.route) {
            CompanyDetailsScreen(
                viewModel = viewModel,
                onBackClick = { registrationNavController.popBackStack() },
                onProceedClick = {
                    if (viewModel.validateCompanyDetails()) {
                        registrationNavController.navigate(Route.SetPassword.route)
                    }
                }
            )
        }

        composable(Route.SetPassword.route) {
            SetPasswordScreen(
                viewModel = viewModel,
                onBackClick = { registrationNavController.popBackStack() },
                onRegisterClick = {
                    registrationNavController.navigate(Route.Success.route) {
                        popUpTo(Route.PersonalDetails.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.Success.route) {
            SuccessScreen(
                onBackToLoginClick = {
                    rootNavController.navigate(LoginRoute.Login.route) {
                        popUpTo(LoginRoute.Registration.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
