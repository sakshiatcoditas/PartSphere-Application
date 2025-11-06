package com.example.partsphere.presentation.distributor.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.presentation.distributor.registration_screen.CompanyDetailsScreen
import com.example.partsphere.presentation.distributor.registration_screen.RegisterScreen
import com.example.partsphere.presentation.distributor.registration_screen.SetPasswordScreen
import com.example.partsphere.presentation.distributor.registration_screen.SuccessScreen
import com.example.partsphere.viewmodel.RegistrationViewModel

@Composable
fun RegistrationNavGraph(navController: NavHostController) {
    val viewModel: RegistrationViewModel = hiltViewModel() // Shared across all screens

    NavHost(
        navController = navController,
        startDestination = Route.PersonalDetails.route
    ) {

        // ------------------- Personal Details -------------------
        composable(Route.PersonalDetails.route) {
            RegisterScreen(
                viewModel = viewModel,
                onProceedClick = {
                    // Validate Personal Details before navigating
                    if (viewModel.validatePersonalDetails()) {
                        navController.navigate(Route.CompanyDetails.route)
                    }
                },
                onLoginClick = {
                    navController.navigate(Route.Login.route)
                }
            )
        }

        // ------------------- Company Details -------------------
        composable(Route.CompanyDetails.route) {
            CompanyDetailsScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onProceedClick = {
                    // Validate Company Details before navigating
                    if (viewModel.validateCompanyDetails()) {
                        navController.navigate(Route.SetPassword.route)
                    }
                }
            )
        }

        //  Set Password Screen
        composable(Route.SetPassword.route) {
            val viewModel: RegistrationViewModel = hiltViewModel() // get the shared ViewModel

            SetPasswordScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack() // goes back to company details
                },
                onRegisterClick = {
                    // final validation already done inside SetPasswordScreen
                    // Navigate to success screen
                    navController.navigate(Route.Success.route) {
                        popUpTo(Route.PersonalDetails.route) { inclusive = true }
                    }
                }
            )
        }


        // ------------------- Success -------------------
        composable(Route.Success.route) {
            SuccessScreen(
                onBackToLoginClick = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(Route.PersonalDetails.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
