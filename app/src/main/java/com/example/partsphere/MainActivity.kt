package com.example.partsphere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.partsphere.navigation.RootNavGraph
import com.example.partsphere.presentation.login_screen.navigation.LoginNavGraph
import com.example.partsphere.presentation.owner.ui.OwnerDashboardScreen
import com.example.partsphere.presentation.owner.ui.OwnerMainScreen
import com.example.partsphere.presentation.owner.ui.ReportsScreen

import com.example.partsphere.ui.theme.PartSphereTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PartSphereTheme {
               val rootNavController = rememberNavController()
              RootNavGraph(rootNavController)


            }
        }

    }
}