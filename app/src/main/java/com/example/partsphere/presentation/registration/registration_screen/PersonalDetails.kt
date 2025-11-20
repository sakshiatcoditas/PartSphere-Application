package com.example.partsphere.presentation.registration.registration_screen

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.InputField
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.utils.Field
import com.example.partsphere.presentation.registration.viewmodel.RegistrationViewModel

@Composable
fun RegisterScreen(
    viewModel: RegistrationViewModel,
    onProceedClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val fieldErrors by remember { derivedStateOf { viewModel.fieldErrors } }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.photoUri = uri
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .padding(padding)
                .padding(top = 70.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create Account",
                fontSize = 28.sp, color = Color.Black,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(10.dp))
            Text("Please enter your details to continue registration", fontSize = 14.sp, color = Color.Gray)
            Spacer(Modifier.height(32.dp))

            // Avatar picker
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.photoUri),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Add Photo", color = Color.DarkGray)
                }
            }
            Text("Profile Photo", color = Color.Black, fontSize = 12.sp)
            Spacer(Modifier.height(24.dp))

            // Input Fields
            InputField(
                label = "Full Name",
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                error = fieldErrors[Field.FULL_NAME]
            )

            Spacer(Modifier.height(16.dp))

            InputField(
                label = "Email",
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                error = fieldErrors[Field.EMAIL]
            )
            Spacer(Modifier.height(16.dp))

            InputField(
                label = "Phone Number",
                value = viewModel.phoneNo,
                onValueChange = { viewModel.phoneNo = it },
                error = fieldErrors[Field.PHONE]
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { if(viewModel.validatePersonalDetails()) onProceedClick() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = Color.White)
            ) { Text("Proceed", fontSize = 16.sp) }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Already have an account? ", color = Color.Black, fontSize = 15.sp)
                Text(
                    "Login",
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )

            }
        }
    }
}
