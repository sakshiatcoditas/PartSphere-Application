package com.example.partsphere.presentation.distributor.registration_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.utils.RegistrationState
import com.example.partsphere.viewmodel.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetPasswordScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val fieldErrors by remember { derivedStateOf { viewModel.fieldErrors } }
    val context = LocalContext.current

// Observe registration state
    val registrationState by remember { derivedStateOf { viewModel.registrationState } }

// Automatically navigate on success
    LaunchedEffect(registrationState) {
        if (registrationState is RegistrationState.Success) {
            onRegisterClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Password", fontSize = 20.sp, color = Black, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Black),
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password", color = Black) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            fieldErrors[com.example.partsphere.utils.Field.PASSWORD]?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                label = { Text("Confirm Password", color = Black) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            fieldErrors[com.example.partsphere.utils.Field.CONFIRM_PASSWORD]?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.performRegistration(context) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = Color.White)
            ) {
                Text("Register", fontSize = 18.sp)
            }

            Spacer(Modifier.height(16.dp))

            // Display state feedback
            when (registrationState) {
                is RegistrationState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }
                is RegistrationState.Error -> {
                    Text(
                        text = (registrationState as RegistrationState.Error).field,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                else -> {}
            }
        }
    }

}
