package com.example.partsphere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.network.PreferenceManager
import com.example.partsphere.presentation.login_screen.DistributorDashboardScreen
import com.example.partsphere.presentation.login_screen.navigation.LoginNavGraph
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.owner.ui.OwnerDashboardScreen
import com.example.partsphere.presentation.owner.ui.OwnerMainScreen
import com.example.partsphere.presentation.registration.navigation.RegistrationNavGraph

@Composable
fun RootNavGraph(rootNavController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = PreferenceManager(context)

    NavHost(
        navController = rootNavController,
        startDestination = LoginRoute.Login.route,
        modifier = modifier
    ) {

        // Login Flow
        composable(LoginRoute.Login.route) {
            LoginNavGraph(rootNavController)
        }

        // Registration Flow
        composable(LoginRoute.Registration.route) {
            RegistrationNavGraph(rootNavController)
        }

        // Owner Flow
        composable("owner_graph") {
            OwnerDashboardScreen(rootNavController)
        }

        // Distributor Flow
        composable("distributor_graph") {
            DistributorDashboardScreen(rootNavController, prefs)
        }
    }
}
