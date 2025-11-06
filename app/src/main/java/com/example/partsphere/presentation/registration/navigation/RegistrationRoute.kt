package com.example.partsphere.presentation.registration.navigation

sealed class RegistrationRoute(val route: String) {


    object PersonalDetails : RegistrationRoute(Route.PersonalDetails.route)
    object CompanyDetails : RegistrationRoute(Route.CompanyDetails.route)
    object SetPassword : RegistrationRoute(Route.SetPassword.route)
    object Success : RegistrationRoute(Route.Success.route)
    object Login : RegistrationRoute(Route.Login.route)
}
