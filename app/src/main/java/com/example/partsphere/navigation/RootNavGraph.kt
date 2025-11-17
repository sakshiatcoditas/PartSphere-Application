package com.example.partsphere.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.network.PreferenceManager
import com.example.partsphere.presentation.login_screen.navigation.LoginNavGraph
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.owner.ui.OwnerMainScreen
import com.example.partsphere.presentation.registration.navigation.RegistrationNavGraph
import com.example.partsphere.presentation.login_screen.DistributorDashboardScreen

@Composable
fun RootNavGraph(rootNavController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = rootNavController,
        startDestination = LoginRoute.Login.route,
        modifier = modifier
    ) {

        // LOGIN FLOW
        composable(LoginRoute.Login.route) {
            // Just delegate to LoginNavGraph
            LoginNavGraph(rootNavController)
        }

        // REGISTRATION FLOW
        composable(LoginRoute.Registration.route) {
            val registrationNavController = rootNavController
            RegistrationNavGraph(navController = registrationNavController)
        }

        // OWNER FLOW
        composable("owner_graph") {
            OwnerMainScreen(rootNavController)
        }

        // DISTRIBUTOR FLOW
        composable("distributor_graph") {
            val context = LocalContext.current
            val prefs = PreferenceManager(context)
            DistributorDashboardScreen(rootNavController, prefs)
        }
    }
}
