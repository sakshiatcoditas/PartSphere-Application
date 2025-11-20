package com.example.partsphere.presentation.owner.navigation

import com.example.partsphere.network.PreferenceManager

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.owner.ui.AddProductScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.ManageEmployeeScreen
import com.example.partsphere.presentation.owner.ui.ManageFactoryScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.CentralOfficerScreen
import com.example.partsphere.presentation.owner.ui.OwnerHomeScreen
import com.example.partsphere.presentation.owner.ui.ProfileScreen
import com.example.partsphere.presentation.owner.ui.ReportsScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.ChiefSupervisorScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.PlantHeadScreen

@Composable
fun OwnerNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController, // added
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = PreferenceManager(context)

    NavHost(
        navController = navController,
        startDestination = OwnerBottomNavItem.Home.route,
        modifier = modifier
    ) {

        composable(OwnerBottomNavItem.Home.route) { OwnerHomeScreen() }
        composable(OwnerBottomNavItem.Reports.route) { ReportsScreen() }

        composable(OwnerBottomNavItem.ManageEmployee.route) {
            ManageEmployeeScreen(
                onCentralOfficerClick = { navController.navigate("central_officers") },
                onPlantHeadClick = { navController.navigate("plant_head") },
                onChiefSupervisorClick = { navController.navigate("chief_supervisor") }
            )
        }

        composable(OwnerBottomNavItem.ManageFactory.route) { ManageFactoryScreen() }

        composable(OwnerBottomNavItem.Profile.route) {
            ProfileScreen(
                context = context,
                onLogoutClick = {
                    prefs.clearToken()
                    prefs.clearRole()

                    rootNavController.navigate(LoginRoute.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("central_officers") { CentralOfficerScreen(onBackClick = { navController.popBackStack() }) }
        composable("plant_head") { PlantHeadScreen(onBackClick = { navController.popBackStack() }) }
        composable("chief_supervisor") { ChiefSupervisorScreen(onBackClick = { navController.popBackStack() }) }
        composable(OwnerBottomNavItem.AddProduct.route) { AddProductScreen() }
    }
}
