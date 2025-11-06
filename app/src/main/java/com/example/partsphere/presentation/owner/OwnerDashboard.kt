package com.example.partsphere.presentation.owner


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Bottom Navigation Destinations
sealed class OwnerBottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : OwnerBottomNavItem("home", Icons.Default.Home, "Home")
    object Reports : OwnerBottomNavItem("reports", Icons.Default.Home, "Reports")
    object ManageEmployee : OwnerBottomNavItem("manage_employee", Icons.Default.Home, "Employees")
    object ManageFactory : OwnerBottomNavItem("manage_factory", Icons.Default.Home, "Factory")
    object Profile : OwnerBottomNavItem("profile", Icons.Default.Person, "Profile")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen() {
    val navController = rememberNavController()
    val items = listOf(
        OwnerBottomNavItem.Home,
        OwnerBottomNavItem.Reports,
        OwnerBottomNavItem.ManageEmployee,
        OwnerBottomNavItem.ManageFactory,
        OwnerBottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label, tint = Color.White) },
                        label = { Text(item.label, color = Color.White, fontSize = 10.sp) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            OwnerNavGraph(navController = navController)
        }
    }

}

@Composable
fun OwnerNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = OwnerBottomNavItem.Home.route) {
        composable(OwnerBottomNavItem.Home.route) { HomeScreen() }
        composable(OwnerBottomNavItem.Reports.route) { ReportsScreen() }
        composable(OwnerBottomNavItem.ManageEmployee.route) { ManageEmployeeScreen() }
        composable(OwnerBottomNavItem.ManageFactory.route) { ManageFactoryScreen() }
        composable(OwnerBottomNavItem.Profile.route) { ProfileScreen() }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Screen", color = Color.Black, fontSize = 24.sp)
    }
}

@Composable
fun ReportsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Reports Screen", color = Color.Black, fontSize = 24.sp)
    }
}

@Composable
fun ManageEmployeeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Manage Employees", color = Color.Black, fontSize = 24.sp)
    }
}

@Composable
fun ManageFactoryScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Manage Factory", color = Color.Black, fontSize = 24.sp)
    }
}

@Composable
fun ProfileScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text("Profile Screen", color = Color.Black, fontSize = 24.sp)
    }
}