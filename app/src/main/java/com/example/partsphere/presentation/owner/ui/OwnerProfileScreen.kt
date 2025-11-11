package com.example.partsphere.presentation.owner.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {}
) {
    var profilePhoto by remember { mutableStateOf<Uri?>(null) }
    var name by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("john@example.com") }
    var designation by remember { mutableStateOf("Manager") }
    var companyName by remember { mutableStateOf("Partsphere") }

    var isEditing by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> if (isEditing) profilePhoto = uri }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray, CircleShape)
                .clickable(enabled = isEditing) { launcher.launch("image/*") },
            contentAlignment = Alignment.BottomEnd
        ) {
            if (profilePhoto != null) {
                Image(
                    painter = rememberAsyncImagePainter(profilePhoto),
                    contentDescription = "Profile Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("Add Photo", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name Field (disabled by default, solid background)
        Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(4.dp))) {
            OutlinedTextField(
                value = name,
                onValueChange = { if (isEditing) name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Email Field
        Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(4.dp))) {
            OutlinedTextField(
                value = email,
                onValueChange = { if (isEditing) email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Designation Field
        Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(4.dp))) {
            OutlinedTextField(
                value = designation,
                onValueChange = { if (isEditing) designation = it },
                label = { Text("Designation") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Company Name Field
        Box(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(4.dp))) {
            OutlinedTextField(
                value = companyName,
                onValueChange = { if (isEditing) companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = isEditing
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout Button
        Button(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Logout", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Edit / Save Button
        Button(
            onClick = { isEditing = !isEditing },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (isEditing) "Save" else "Edit Profile", color = Color.White)
        }
    }
}
