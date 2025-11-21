package com.example.partsphere.presentation.registration.registration_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.partsphere.R
import com.example.partsphere.presentation.InputField
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.utils.Field
import com.example.partsphere.utils.StatesList
import com.example.partsphere.viewmodel.RegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailsScreen(
    viewModel: RegistrationViewModel,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit
) {
    val fieldErrors by remember { derivedStateOf { viewModel.fieldErrors } }
    val stateOptions = StatesList.indianStatesAndUTs
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.company_details_title), fontSize = 20.sp, color = Black, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
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
                .padding(padding)
                .padding(top = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            InputField(
                label = stringResource(R.string.company_business_name),
                value = viewModel.companyName,
                onValueChange = { viewModel.companyName = it },
                error = fieldErrors[Field.COMPANY_NAME]
            )
            Spacer(Modifier.height(16.dp))

            InputField(
                label = stringResource(R.string.gst_id),
                value = viewModel.gstId,
                onValueChange = { viewModel.gstId = it },
                error = fieldErrors[Field.GST_ID]
            )
            Spacer(Modifier.height(16.dp))

            InputField(
                label = stringResource(R.string.address),
                value = viewModel.companyAddress,
                onValueChange = { viewModel.companyAddress = it },
                error = fieldErrors[Field.ADDRESS]
            )
            Spacer(Modifier.height(16.dp))

            InputField(
                label = stringResource(R.string.city),
                value = viewModel.city,
                onValueChange = { viewModel.city = it },
                error = fieldErrors[Field.CITY]
            )
            Spacer(Modifier.height(16.dp))

            // State Dropdown

            // State Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = viewModel.state,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.state), color = Black) },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor() // ensures dropdown shows below
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        cursorColor = Black
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                ) {
                    stateOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.Black) },
                            onClick = {
                                viewModel.state = option
                                expanded = false
                            }
                        )
                    }
                }
            }

        }
        fieldErrors[Field.STATE]?.let { error ->
            Text(text = error, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(Modifier.height(16.dp))

        InputField(
            label = stringResource(R.string.pincode),
            value = viewModel.pinCode,
            onValueChange = { viewModel.pinCode = it },
            error = fieldErrors[Field.PINCODE],
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { if (viewModel.validateCompanyDetails()) onProceedClick() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black, contentColor = Color.White)
        ) {     Text(stringResource(R.string.next), fontSize = 16.sp)
        }

        }
    }

