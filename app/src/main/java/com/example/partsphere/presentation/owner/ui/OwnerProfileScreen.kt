package com.example.partsphere.presentation.owner.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.partsphere.presentation.owner.data.ProfilePreferences


import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    context: Context,
    onLogoutClick: () -> Unit = {}
) {
    val profilePrefs = remember { ProfilePreferences(context) }

    var profilePhoto by remember {
        mutableStateOf(
            profilePrefs.profileUri?.let { path ->
                val file = File(path)
                if (file.exists()) Uri.fromFile(file) else null
            }
        )
    }
    var name by remember { mutableStateOf(profilePrefs.name ?: "John Doe") }
    var email by remember { mutableStateOf(profilePrefs.email ?: "john@example.com") }
    var designation by remember { mutableStateOf(profilePrefs.designation ?: "Manager") }
    var companyName by remember { mutableStateOf(profilePrefs.company ?: "Partsphere") }
    var isEditing by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (isEditing && uri != null) {
                val savedPath = saveProfileImageToInternalStorage(context, uri)
                if (savedPath != null) {
                    profilePhoto = Uri.fromFile(File(savedPath))
                }
            }
        }
    )

    val neutralTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Gray,
        unfocusedBorderColor = Color.Gray,
        errorBorderColor = Color.Gray,
        focusedLabelColor = Color.DarkGray,
        unfocusedLabelColor = Color.DarkGray,
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        cursorColor = Color.Black,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset(y = 120.dp)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            // Avatar Circle
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable(enabled = isEditing) { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
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

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Fields
            OutlinedTextField(
                value = name,
                onValueChange = { if (isEditing) name = it },
                label = { Text("Name") },
                singleLine = true,
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                colors = neutralTextFieldColors
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { if (isEditing) email = it },
                label = { Text("Email") },
                singleLine = true,
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                colors = neutralTextFieldColors
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = designation,
                onValueChange = { if (isEditing) designation = it },
                label = { Text("Designation") },
                singleLine = true,
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                colors = neutralTextFieldColors
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = companyName,
                onValueChange = { if (isEditing) companyName = it },
                label = { Text("Company Name") },
                singleLine = true,
                readOnly = !isEditing,
                modifier = Modifier.fillMaxWidth(),
                colors = neutralTextFieldColors
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        if (isEditing) {
                            profilePrefs.name = name
                            profilePrefs.email = email
                            profilePrefs.designation = designation
                            profilePrefs.company = companyName
                            profilePrefs.profileUri = profilePhoto?.path
                        }
                        isEditing = !isEditing
                    },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = if (isEditing) "Save" else "Edit Profile", color = Color.White)
                }

                OutlinedButton(
                    onClick = onLogoutClick,
                    shape = RoundedCornerShape(20.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp)
                ) {
                    Text("Logout", color = Color.Black)
                }
            }
        }
    }
}

fun saveProfileImageToInternalStorage(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "profile_photo.jpg")
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}





