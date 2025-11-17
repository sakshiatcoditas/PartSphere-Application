package com.example.partsphere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.partsphere.network.PreferenceManager
import com.example.partsphere.presentation.login_screen.LoginScreen
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.login_screen.DistributorDashboardScreen
import com.example.partsphere.presentation.owner.ui.OwnerMainScreen
import com.example.partsphere.presentation.registration.navigation.RegistrationNavGraph

//Updated application

@Composable
fun RootNavGraph(rootNavController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = rootNavController,
        startDestination = "login_graph",
        modifier = modifier
    ) {

        // LOGIN GRAPH
        navigation(
            startDestination = LoginRoute.Login.route,
            route = "login_graph"
        ) {

            composable(LoginRoute.Login.route) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val prefs = loginViewModel.getPrefs()

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
                                    popUpTo("login_graph") { inclusive = true }
                                }
                            }
                            "DISTRIBUTOR" -> {
                                rootNavController.navigate("distributor_graph") {
                                    popUpTo("login_graph") { inclusive = true }
                                }
                            }

                        }
                    }
                )
            }

            //  Registration Flow
            composable(LoginRoute.Registration.route) {
                val registrationNavController = rememberNavController()
                RegistrationNavGraph(navController = registrationNavController)
            }



        }

        // OWNER FLOW
        composable("owner_graph") {
            OwnerMainScreen(rootNavController = rootNavController)
        }

        // DISTRIBUTOR FLOW
        composable("distributor_graph") {
            val context = LocalContext.current
            val prefs = PreferenceManager(context)
            DistributorDashboardScreen(
                navController = rootNavController,
                prefs = prefs
            )
        }
    }
}
