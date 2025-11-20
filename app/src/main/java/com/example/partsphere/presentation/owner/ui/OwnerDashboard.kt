package com.example.partsphere.presentation.owner.ui
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.owner.navigation.OwnerBottomNavItem
import com.example.partsphere.presentation.owner.navigation.OwnerNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen(rootNavController: NavHostController) {

    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                listOf(
                    OwnerBottomNavItem.Home,
                    OwnerBottomNavItem.Reports,
                    OwnerBottomNavItem.ManageEmployee,
                    OwnerBottomNavItem.ManageFactory,

                    OwnerBottomNavItem.AddProduct,
                    OwnerBottomNavItem.Profile,
                ).forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            bottomNavController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                painterResource(id = item.iconRes),
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF333333)
                        )
                    )
                }
            }
        }
    ) { padding ->
        OwnerNavGraph(
            navController = bottomNavController,
            rootNavController = rootNavController,
            modifier = Modifier.padding(padding)
        )
    }
}