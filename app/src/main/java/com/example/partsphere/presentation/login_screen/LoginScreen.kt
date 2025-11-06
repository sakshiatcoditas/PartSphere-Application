package com.example.partsphere.presentation.login_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import com.example.partsphere.R
import com.example.partsphere.ui.theme.Black
import androidx.compose.runtime.collectAsState
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
    val snackbarHostState = remember { SnackbarHostState() }

    // Collect auth state from ViewModel
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
            Text(
                text = stringResource(R.string.welcome_back),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email), color = Color.Black) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    cursorColor = Black
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password), color = Color.Black) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Black,
                    unfocusedBorderColor = Black,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    cursorColor = Black
                )
            )

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onNavigateToForgotPassword, modifier = Modifier.align(Alignment.End)) {
                Text(text = stringResource(R.string.forgot_password), color = Black)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email, password) },
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
                Text(stringResource(R.string.login), fontSize = 18.sp)
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.dont_have_account), color = Black)
            }

            if (viewModel.authState.collectAsState().value is AuthState.Loading) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(color = Black)
            }
        }
    }
}
