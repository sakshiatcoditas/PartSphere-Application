package com.example.partsphere.presentation.owner.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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

    object AddProduct : OwnerBottomNavItem("add_product", Icons.Default.Add, "Products")
}

