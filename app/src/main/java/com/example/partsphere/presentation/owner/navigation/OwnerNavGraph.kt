package com.example.partsphere.presentation.owner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.presentation.owner.ui.ManageEmployeeScreen
import com.example.partsphere.presentation.owner.ui.ManageFactoryScreen
import com.example.partsphere.presentation.owner.navigation.OwnerBottomNavItem
import com.example.partsphere.presentation.owner.ui.CentralOfficerScreen
import com.example.partsphere.presentation.owner.ui.OwnerHomeScreen
import com.example.partsphere.presentation.owner.ui.PlantHeadScreen
import com.example.partsphere.presentation.owner.ui.ProfileScreen
import com.example.partsphere.presentation.owner.ui.ReportsScreen

@Composable
fun OwnerNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = OwnerBottomNavItem.Home.route) {



        composable(OwnerBottomNavItem.Home.route) { OwnerHomeScreen() }
        composable(OwnerBottomNavItem.Reports.route) { ReportsScreen() }

        composable(OwnerBottomNavItem.ManageEmployee.route) {
            ManageEmployeeScreen(
                onCentralOfficerClick = { navController.navigate("central_officers") },
                onPlantHeadClick = { navController.navigate("plant_heads") }
            )
        }
        composable(OwnerBottomNavItem.ManageFactory.route) { ManageFactoryScreen() }
        composable(OwnerBottomNavItem.Profile.route) { ProfileScreen() }

        // Central Officers screen
        composable("central_officers") {
            CentralOfficerScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

// Plant Heads screen
        composable("plant_heads") { PlantHeadScreen() }

    }
}