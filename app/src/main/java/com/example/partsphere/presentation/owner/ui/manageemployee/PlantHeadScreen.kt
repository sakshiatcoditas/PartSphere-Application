package com.example.partsphere.presentation.owner.ui.manageemployee

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.presentation.owner.viewmodel.ManageFactoryViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.partsphere.ui.theme.Black


data class PlantHead(
    val id: Int,
    val name: String,
    val email: String,
    val designation: String,
    val factory: String,
    val photoUri: Uri? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantHeadScreen(
    onBackClick: () -> Unit = {},
    viewModel: ManageFactoryViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var currentEditingHead by remember { mutableStateOf<PlantHead?>(null) }

    val plantHeadsApi = viewModel.plantHeads
    val loading = viewModel.loading
    val error = viewModel.error
    val context = LocalContext.current

    val allFactories = listOf("Mumbai Plant", "Pune Plant", "Delhi Plant")

    // Fetch API once
    LaunchedEffect(Unit) { viewModel.fetchPlantHeads() }

    // Filtered list based on search
    val filteredHeads = plantHeadsApi.filter { it.username.contains(searchText, ignoreCase = true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "Plant Heads",
                        fontWeight = FontWeight.Bold,   //  Make title bold
                        color = Color.Black
                    )                        },
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
                placeholderText = "Search Plant Heads"
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.Black)
                    }
                }

                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Error: $error", color = Color.Red)
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (filteredHeads.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No results found", fontSize = 16.sp, color = Color.Gray)
                                }
                            }
                        } else {
                            items(filteredHeads) { head ->
                                PlantHeadCard(
                                    plantHead = PlantHead(
                                        id = head.id,
                                        name = head.username,
                                        email = head.email,
                                        designation = head.role,
                                        factory = head.factory ?: "",
                                        photoUri = null
                                    ),
                                    onEdit = {
                                        currentEditingHead = PlantHead(
                                            id = head.id,
                                            name = head.username,
                                            email = head.email,
                                            designation = head.role,
                                            factory = head.factory ?: "",
                                            photoUri = null
                                        )
                                        showDialog = true
                                    },
                                    onDelete = {
                                        viewModel.deletePlantHead(head.id) {
                                            Toast.makeText(
                                                context,
                                                "Deleted ${head.username}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                currentEditingHead = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Plant Head", tint = Color.White)
        }
    }

    // Add/Edit Dialog
    if (showDialog) {
        AddPlantHeadDialog(
            allFactories = allFactories,
            initialData = currentEditingHead,
            onDismiss = { showDialog = false },
            onAdd = { name, email, designation, factory, photoUri ->
                // For simplicity, just update local state; API integration for add/edit can be added later
                showDialog = false
            }
        )
    }
}


// ---------------- ADD / EDIT DIALOG ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantHeadDialog(
    allFactories: List<String>,
    onDismiss: () -> Unit,
    onAdd: (name: String, email: String, designation: String, factory: String, photoUri: Uri?) -> Unit,
    initialData: PlantHead? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialData?.name ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(initialData?.email ?: "")) }
    var designation by remember { mutableStateOf(initialData?.designation ?: "") }
    var designationExpanded by remember { mutableStateOf(false) }
    var selectedFactory by remember { mutableStateOf(initialData?.factory ?: "") }
    var factoryExpanded by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf(initialData?.photoUri) }

    // Validation states
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    val designations = listOf("Plant-Head", "Chief-Supervisor")
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
                    text = if (initialData != null) "Edit Plant Head" else "Add New Plant Head",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Photo
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

                // Name Field

                OutlinedTextField(
                    value = name,
                    onValueChange = { input ->
                        name = input
                        // Validation: must be letters and spaces only, and not blank
                        nameError = input.text.isBlank() || !input.text.matches(Regex("^[a-zA-Z\\s]*$"))
                    },
                    label = { Text("Full Name") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )

                if (nameError) {
                    Text("Full Name must contain letters only", color = Color.Red, fontSize = 12.sp)
                }



                Spacer(modifier = Modifier.height(8.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = it.text.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(it.text).matches()
                    },
                    label = { Text("Email") },
                    isError = emailError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError) {
                    Text("Enter a valid email", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Designation Dropdown
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
                        designations.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    designation = option
                                    designationExpanded = false
                                }
                            )
                        }
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
                        allFactories.forEach { factory ->
                            DropdownMenuItem(
                                text = { Text(factory) },
                                onClick = {
                                    selectedFactory = factory
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
                        onClick = {
                            // Trigger validation
                            nameError = name.text.isBlank()
                            emailError = email.text.isBlank() ||
                                    !android.util.Patterns.EMAIL_ADDRESS.matcher(email.text).matches()

                            if (!nameError && !emailError) {
                                onAdd(name.text, email.text, designation, selectedFactory, photoUri)
                            }
                        }
                    ) {
                        Text(if (initialData != null) "Update" else "Add")
                    }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}


// ---------------- PLANT HEAD CARD ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantHeadCard(
    plantHead: PlantHead,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, tween(100))
    val overlayColor by animateColorAsState(if (isPressed) Color(0x33000000) else Color.Transparent, tween(150))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(overlayColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (plantHead.photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(plantHead.photoUri),
                            contentDescription = "Plant Head Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("No Photo", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(plantHead.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Email: ${plantHead.email}", fontSize = 14.sp, color = Color.DarkGray)
                    Text("Designation: ${plantHead.designation}", fontSize = 14.sp, color = Color.DarkGray)
                    Text("Factory: ${plantHead.factory}", fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) { Text("Edit") }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) { Text("Delete") }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Plant Head") },
            text = { Text("Are you sure you want to delete ${plantHead.name}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }
                ) { Text("Cancel", color = Color.Gray) }
            }
        )
    }
}

