package com.example.partsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.partsphere.presentation.owner.ui.OwnerDashboardScreen

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
