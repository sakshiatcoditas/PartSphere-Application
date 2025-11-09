package com.example.partsphere.presentation.owner.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.partsphere.presentation.owner.ui.manageemployee.ManageEmployeeScreen
import com.example.partsphere.presentation.owner.ui.ManageFactoryScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.CentralOfficerScreen
import com.example.partsphere.presentation.owner.ui.OwnerHomeScreen
import com.example.partsphere.presentation.owner.ui.ProfileScreen
import com.example.partsphere.presentation.owner.ui.ReportsScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.ChiefSupervisorScreen
import com.example.partsphere.presentation.owner.ui.manageemployee.PlantHeadScreen

@Composable
fun OwnerNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = OwnerBottomNavItem.Home.route) {

        composable(OwnerBottomNavItem.Home.route) { OwnerHomeScreen() }
        composable(OwnerBottomNavItem.Reports.route) { ReportsScreen() }

        composable(OwnerBottomNavItem.ManageEmployee.route) {
            ManageEmployeeScreen(
                onCentralOfficerClick = { navController.navigate("central_officers") },
                onPlantHeadClick = { navController.navigate("plant_head") },       // updated
                onChiefSupervisorClick = { navController.navigate("chief_supervisor") } // new
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

        // Plant Head screen
        composable("plant_head") {
            PlantHeadScreen(
                onBackClick = { navController.popBackStack() }

            )
        }

        // Chief Supervisor screen
        composable("chief_supervisor") {
            ChiefSupervisorScreen(
                onBackClick = { navController.popBackStack() }

            )
        }
    }
}
