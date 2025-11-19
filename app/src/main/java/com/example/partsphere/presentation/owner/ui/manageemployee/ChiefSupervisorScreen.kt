package com.example.partsphere.presentation.owner.ui.manageemployee

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.owner.model.AddChiefSupervisorResponse
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.presentation.owner.viewmodel.ManageFactoryViewModel

// ---------------- DATA CLASS ----------------
//data class ChiefSupervisor(
//    val name: String,
//    val email: String,
//    val designation: String,
//    val factory: String,
//    val photoUri: Uri? = null
//)

// ---------------- CHIEF SUPERVISOR SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiefSupervisorScreen(
    viewModel: ManageFactoryViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.chiefUiState.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var currentEditingSupervisor by remember { mutableStateOf<AddChiefSupervisorResponse?>(null) }

    val listState = rememberLazyListState()

    // Fetch supervisors and factories when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchChiefSupervisors(loadMore = false)
        viewModel.fetchFactoriesForSupervisor()
    }

    // Pagination
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                val total = uiState.supervisors.size
                if (lastVisibleIndex != null && lastVisibleIndex >= total - 1 && !uiState.isLoading) {
                    viewModel.fetchChiefSupervisors(loadMore = true)
                }
            }
    }

    val filteredSupervisors = uiState.supervisors.filter {
        it.username.contains(searchText, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            TopAppBar(
                title = { Text("Chief Supervisors") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            SearchBar(
                query = searchText,
                onQueryChange = { searchText = it },
                placeholderText = "Search Supervisors"
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading && uiState.supervisors.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.error ?: "Something went wrong",
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSupervisors, key = { it.id }) {
                            supervisor ->
                            PlantHeadCard(
                                plantHead = PlantHead(
                                    name = supervisor.username,
                                    email = supervisor.email,
                                    designation = supervisor.role,
                                    factory = supervisor.factoryName ?: "Unassigned",
                                    photoUri = supervisor.photo?.let { Uri.parse(it) }
                                ),
                                onEdit = {
                                    currentEditingSupervisor = supervisor
                                    showDialog = true
                                },
                                onDelete = {
                                    // Delete logic to implement later
                                }
                            )
                        }

                        if (uiState.isLoading && uiState.supervisors.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                currentEditingSupervisor = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Supervisor", tint = Color.White)
        }
    }

    if (showDialog) {
        AddChiefSupervisorDialog(
            viewModel = viewModel,
            initialData = currentEditingSupervisor?.let { supervisor ->
                PlantHead(
                    name = supervisor.username,
                    email = supervisor.email,
                    designation = supervisor.role,
                    factory = supervisor.factoryName ?: "",
                    photoUri = supervisor.photo?.let { Uri.parse(it) }
                )
            },
            onDismiss = { showDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChiefSupervisorDialog(
    viewModel: ManageFactoryViewModel,
    onDismiss: () -> Unit,
    initialData: PlantHead? = null
) {
    val factories by viewModel.supervisorFactories.collectAsState()
// API-fetched factories

    var name by remember { mutableStateOf(TextFieldValue(initialData?.name ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(initialData?.email ?: "")) }
    var designation by remember { mutableStateOf(initialData?.designation ?: "Chief-Supervisor") }
    var designationExpanded by remember { mutableStateOf(false) }
    var selectedFactory by remember { mutableStateOf(initialData?.factory ?: "") }
    var factoryExpanded by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf(initialData?.photoUri) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> photoUri = uri }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (initialData != null) "Edit Chief Supervisor" else "Add New Chief Supervisor",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Photo Upload
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.LightGray, CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Selected Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Upload Photo", fontSize = 14.sp, color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name & Email
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Designation Dropdown (fixed)
                ExposedDropdownMenuBox(
                    expanded = designationExpanded,
                    onExpandedChange = { designationExpanded = !designationExpanded }
                ) {
                    OutlinedTextField(
                        value = designation,
                        onValueChange = {},
                        label = { Text("Designation") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(designationExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = designationExpanded,
                        onDismissRequest = { designationExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Chief-Supervisor") },
                            onClick = {
                                designation = "Chief-Supervisor"
                                designationExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Factory Dropdown
                ExposedDropdownMenuBox(
                    expanded = factoryExpanded,
                    onExpandedChange = { factoryExpanded = !factoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedFactory,
                        onValueChange = {},
                        label = { Text("Factory Associated") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(factoryExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                    )
                    ExposedDropdownMenu(
                        expanded = factoryExpanded,
                        onDismissRequest = { factoryExpanded = false }
                    ) {
                        factories.forEach { factory ->
                            DropdownMenuItem(
                                text = { Text(factory.name) },
                                onClick = {
                                    selectedFactory = factory.name
                                    factoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        enabled = !isLoading,
                        onClick = {
                            val factoryId =
                                factories.firstOrNull { it.name == selectedFactory }?.id ?: 0L

                            if (name.text.isNotBlank() && email.text.isNotBlank() && factoryId != 0L) {
                                isLoading = true
                                viewModel.addChiefSupervisor(
                                    name.text,
                                    email.text,
                                    factoryId,
                                    selectedFactory, // pass the factory name here
                                    photoUri
                                ) { success, message ->
                                    isLoading = false
                                    if (success) onDismiss()
                                    else println("Add Supervisor failed: $message")
                                }
                            }

                        }
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        else Text("Add")
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}