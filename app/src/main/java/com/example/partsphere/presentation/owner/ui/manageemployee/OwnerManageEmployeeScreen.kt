package com.example.partsphere.presentation.owner.ui.manageemployee


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ManageEmployeeScreen(
    onCentralOfficerClick: () -> Unit,
    onPlantHeadClick: () -> Unit,
    onChiefSupervisorClick: () -> Unit // New callback for Chief Supervisor
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Manage Employees",
            color = Color.Black,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        EmployeeCard(
            title = "Add New Central Officer",
            onClick = onCentralOfficerClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmployeeCard(
            title = "Add New Plant Head",
            onClick = onPlantHeadClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmployeeCard(
            title = "Add New Chief Supervisor",
            onClick = onChiefSupervisorClick
        )
    }
}


@Composable
fun EmployeeCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                color = Color.Black
            )
        }
    }
}






