package com.example.partsphere.presentation.registration.registration_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partsphere.R
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.utils.RegistrationState
import com.example.partsphere.utils.Field
import com.example.partsphere.presentation.registration.viewmodel.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetPasswordScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current

    val registrationState = viewModel.registrationState
    val fieldErrors = viewModel.fieldErrors

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Black
                        )
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
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (passwordVisible) R.drawable.visibility else R.drawable.visibility_off
                            ),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            fieldErrors[Field.PASSWORD]?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(16.dp))

            //  CONFIRM PASSWORD WITH VISIBILITY TOGGLE
            OutlinedTextField(
                value = viewModel.confirmPassword,
                onValueChange = { viewModel.confirmPassword = it },
                label = { Text("Confirm Password", color = Black) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            painter = painterResource(
                                id = if (confirmPasswordVisible) R.drawable.visibility else R.drawable.visibility_off
                            ),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            fieldErrors[Field.CONFIRM_PASSWORD]?.let { error ->
                Text(text = error, color = Color.Red, fontSize = 12.sp)
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.performRegistration(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = Color.White)
            ) {
                Text("Register", fontSize = 18.sp)
            }

            Spacer(Modifier.height(16.dp))

            when (registrationState) {
                is RegistrationState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))

                is RegistrationState.Error ->
                    Text(registrationState.field, color = Color.Red, modifier = Modifier.padding(top = 16.dp))

                else -> {}
            }
        }
    }
}
