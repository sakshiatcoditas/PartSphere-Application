package com.example.partsphere.presentation.owner.ui.manageemployee

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class EmployeeOption(
    val title: String,
    val onClick: () -> Unit
)

@Composable
fun ManageEmployeeScreen(
    onCentralOfficerClick: () -> Unit,
    onPlantHeadClick: () -> Unit,
    onChiefSupervisorClick: () -> Unit
) {
    val options = listOf(
        EmployeeOption("Add New Central Officer", onCentralOfficerClick),
        EmployeeOption("Add New Plant Head", onPlantHeadClick),
        EmployeeOption("Add New Chief Supervisor", onChiefSupervisorClick)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)) // subtle gray background
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 24.dp)
        ) {
            // Title
            item {
                Text(
                    text = "Manage Employees",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Employee cards
            items(options) { item ->
                EmployeeCard(title = item.title, onClick = item.onClick)
            }
        }
    }
}


@Composable
fun EmployeeCard(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(120.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF222222),
                maxLines = 2,
                textAlign = TextAlign.Center
            )
        }
    }
}


