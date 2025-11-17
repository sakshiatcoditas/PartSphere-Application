package com.example.partsphere.presentation.login_screen.navigation


sealed class LoginRoute(val route: String) {

    object Login : LoginRoute("login")
    object Registration : LoginRoute("registration") // Used to navigate to RegistrationNavGraph
    object Home : LoginRoute("home")
    object DistributorDashboard : LoginRoute("distributor_dashboard")
    object OwnerDashboard : LoginRoute("owner_dashboard")
    object OwnerMain : LoginRoute("owner_main")




}
