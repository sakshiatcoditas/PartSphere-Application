package com.example.partsphere.presentation.login_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.partsphere.R
import com.example.partsphere.network.PreferenceManager
import com.example.partsphere.presentation.login_screen.navigation.LoginRoute
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel

@Composable
fun DistributorDashboardScreen(
    navController: NavController,
    prefs: PreferenceManager
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.distributor_dashboard_title),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.distributor_dashboard_message),
            color = Color.Gray)

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                prefs.clearToken()
                prefs.clearRole()

                navController.navigate(LoginRoute.Login.route) {
                    popUpTo(LoginRoute.DistributorDashboard.route) { inclusive = true }
                }
            }
        ) {
            Text(text = stringResource(R.string.logout))
        }
    }
}
