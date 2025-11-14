package com.example.partsphere.presentation.owner.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import com.example.partsphere.R

sealed class OwnerBottomNavItem(
    val route: String,
    @DrawableRes val iconRes: Int,

    val label: String)
{
    object Home : OwnerBottomNavItem("home",
            R.drawable.home,
        "Home")

    object Reports : OwnerBottomNavItem("reports",
        R.drawable.reports, "Reports")

    object ManageEmployee : OwnerBottomNavItem(
        "manage_employee",
        R.drawable.employees,
        "Employees")
    object ManageFactory : OwnerBottomNavItem(
        "manage_factory",
        R.drawable.factory,
        "Factory")
    object Profile : OwnerBottomNavItem("profile"
        , R.drawable.profile, "Profile")

    object AddProduct : OwnerBottomNavItem(
        "add_product",
        R.drawable.addproducts, "Products")
}

