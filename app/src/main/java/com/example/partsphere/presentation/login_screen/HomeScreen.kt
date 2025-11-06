package com.example.partsphere.presentation.login_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(homeViewModel: HomeViewModel = hiltViewModel()) {
    val token = homeViewModel.token

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login Successful!", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Saved Token:", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = token, fontSize = 14.sp)
    }
}
