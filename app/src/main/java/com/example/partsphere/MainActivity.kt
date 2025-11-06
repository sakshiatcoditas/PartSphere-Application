package com.example.partsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.partsphere.presentation.login_screen.DistributorDashboardScreen
import com.example.partsphere.presentation.login_screen.navigation.LoginNavGraph
import com.example.partsphere.presentation.owner.OwnerDashboardScreen
import com.example.partsphere.presentation.registration.navigation.RegistrationNavGraph

import com.example.partsphere.ui.theme.PartSphereTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PartSphereTheme {


//                val navController = rememberNavController()
                //RegistrationNavGraph(navController = navController)
//                LoginNavGraph(navController = navController)
                OwnerDashboardScreen()
            }
        }
    }
}
