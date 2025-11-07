package com.example.partsphere.presentation.owner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class OwnerBottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String)
{
    object Home : OwnerBottomNavItem("home", Icons.Default.Home, "Home")
    object Reports : OwnerBottomNavItem("reports", Icons.Default.Home, "Reports")
    object ManageEmployee : OwnerBottomNavItem("manage_employee", Icons.Default.Home, "Employees")
    object ManageFactory : OwnerBottomNavItem("manage_factory", Icons.Default.Home, "Factory")
    object Profile : OwnerBottomNavItem("profile", Icons.Default.Person, "Profile")
}

//object Home : OwnerBottomNavItem("owner_home", "Home", Icons.Default.Home)
//object Reports : OwnerBottomNavItem("owner_reports", "Reports", Icons.Default.BarChart)
//object ManageEmployee : OwnerBottomNavItem("owner_manage_employee", "Employees", Icons.Default.People)
//object ManageFactory : OwnerBottomNavItem("owner_manage_factory", "Factories", Icons.Default.Factory)
//object Profile : OwnerBottomNavItem("owner_profile", "Profile", Icons.Default.Person)