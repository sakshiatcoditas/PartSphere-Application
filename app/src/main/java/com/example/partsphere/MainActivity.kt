package com.example.partsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.partsphere.presentation.distributor.navigation.RegistrationNavGraph

import com.example.partsphere.presentation.distributor.registration_screen.RegisterScreen
import com.example.partsphere.ui.theme.PartSphereTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PartSphereTheme {


                val navController = rememberNavController()
                RegistrationNavGraph(navController = navController)
            }
        }
    }
}
