package com.example.partsphere.presentation.login_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import com.example.partsphere.R
import com.example.partsphere.ui.theme.Black
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import com.example.partsphere.presentation.login_screen.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: (role: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.authState.collectLatest { state ->
            when (state) {
                is AuthState.Error -> snackbarHostState.showSnackbar(state.message)
                is AuthState.Success -> {
                    val role = state.role ?: "UNKNOWN"
                    onLoginSuccess(role)
                }
                else -> {}
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Title
            Text(
                text = "Welcome Back",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(32.dp))

            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = { Text("Email", color = Color.Black) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if(emailError != null) Color.Red else Black,
                    unfocusedBorderColor = if(emailError != null) Color.Red else Black,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    cursorColor = Black
                )
            )
            if (emailError != null) {
                Text(
                    text = emailError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = { Text("Password", color = Color.Black) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if(passwordError != null) Color.Red else Black,
                    unfocusedBorderColor = if(passwordError != null) Color.Red else Black,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    cursorColor = Black
                )
            )
            if (passwordError != null) {
                Text(
                    text = passwordError ?: "",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Forgot password
            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Forgot Password?",
                    color = Black
                )
            }

            Spacer(Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    var valid = true
                    if (email.isBlank()) {
                        emailError = "Email cannot be empty"
                        valid = false
                    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Invalid email format"
                        valid = false
                    }

                    if (password.isBlank()) {
                        passwordError = "Password cannot be empty"
                        valid = false
                    } else if (password.length < 6) {
                        passwordError = "Password must be at least 6 characters"
                        valid = false
                    }

                    if (valid) {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Black,
                    contentColor = Color.White
                ),
                enabled = viewModel.authState.collectAsState().value !is AuthState.Loading
            ) {
                Text("Login", fontSize = 18.sp)
            }

            Spacer(Modifier.height(32.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
                Text(
                    text = "  OR  ",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
            }

            Spacer(Modifier.height(16.dp))

            // Signup as Distributor
            TextButton(
                onClick = { onNavigateToRegister() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Signup as ",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Distributor",
                        color = Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Loading Indicator
            if (viewModel.authState.collectAsState().value is AuthState.Loading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(color = Black)
            }
        }
    }
}

