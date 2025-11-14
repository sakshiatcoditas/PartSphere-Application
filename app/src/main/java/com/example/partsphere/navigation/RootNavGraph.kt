package com.example.partsphere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.partsphere.network.PreferenceManager
import com.example.partsphere.presentation.login_screen.HomeScreen
import com.example.partsphere.presentation.login_screen.LoginScreen
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.login_screen.DistributorDashboardScreen
import com.example.partsphere.presentation.owner.ui.OwnerMainScreen

@Composable
fun RootNavGraph(rootNavController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = rootNavController,
        startDestination = "login_graph",
        modifier = modifier
    ) {

        // ---- LOGIN GRAPH (nested navigation) ----
        navigation(
            startDestination = LoginRoute.Login.route,
            route = "login_graph"
        ) {
            // Login screen
            composable(LoginRoute.Login.route) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val prefs = loginViewModel.getPrefs()

                LoginScreen(
                    viewModel = loginViewModel,
                    onNavigateToRegister = {
                        // you can handle registration route here (if you add it)
                        rootNavController.navigate(LoginRoute.Registration.route)
                    },
                    onNavigateToForgotPassword = { /* TODO */ },
                    onLoginSuccess = { role ->
                        when (role.uppercase()) {
                            "OWNER" -> {
                                // navigate to owner flow
                                rootNavController.navigate("owner_graph") {
                                    popUpTo("login_graph") { inclusive = true }
                                }
                            }
                            "DISTRIBUTOR" -> {
                                rootNavController.navigate("distributor_graph") {
                                    popUpTo("login_graph") { inclusive = true }
                                }
                            }
                            else -> {
                                rootNavController.navigate(LoginRoute.Home.route) {
                                    popUpTo("login_graph") { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            // Registration / Home composables inside login_graph (optional)
            composable(LoginRoute.Registration.route) {
                // TODO: Registration UI
            }

            composable(LoginRoute.Home.route) {
                HomeScreen()
            }
        }

        // ---- OWNER FLOW (top-level composable) ----
        // OwnerMainScreen will create its own internal NavHost for bottom tabs (that's fine)
        composable("owner_graph") {
            OwnerMainScreen(rootNavController = rootNavController)
        }

        // ---- DISTRIBUTOR FLOW ----
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
