package com.example.partsphere.presentation.owner.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

// --- Data model ---
data class CentralOfficer(
    val name: String,
    val email: String,
    val photoUri: Uri? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralOfficerScreen(
    onBackClick: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var officers by remember { mutableStateOf(listOf<CentralOfficer>()) }
    var officerToEdit by remember { mutableStateOf<CentralOfficer?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Central Officers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color.White,
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (officers.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillParentMaxSize()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No Central Officers found", color = Color.Gray)
                                }
                            }
                        } else {
                            items(officers) { officer ->

                                // Inside CentralOfficerScreen -> LazyColumn items
                                CentralOfficerCard(
                                    officer = officer,
                                    onEdit = { updatedOfficer ->
                                        officers = officers.map {
                                            if (it == officer) updatedOfficer else it
                                        }
                                        showDialog = false
                                    },
                                    onDelete = { selected ->
                                        officers = officers.filter { it != selected }
                                    }
                                )
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = {
                        officerToEdit = null // Adding new
                        showDialog = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp),
                    containerColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Officer", tint = Color.White)
                }
            }

            if (showDialog) {
                AddCentralOfficerDialog(
                    onDismiss = { showDialog = false },
                    onAdd = { name, email, photoUri ->
                        if (officerToEdit != null) {
                            // Edit existing
                            officers = officers.map {
                                if (it == officerToEdit) CentralOfficer(name, email, photoUri)
                                else it
                            }
                        } else {
                            // Add new
                            officers = officers + CentralOfficer(name, email, photoUri)
                        }
                        showDialog = false
                    },
                    initialData = officerToEdit // Pass existing data for editing
                )
            }
        }
    )
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralOfficerCard(
    officer: CentralOfficer,
    onEdit: (CentralOfficer) -> Unit,
    onDelete: (CentralOfficer) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
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
                        .background(Color.LightGray, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    officer.photoUri?.let {
                        Image(
                            painter = rememberAsyncImagePainter(it),
                            contentDescription = officer.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: Text("No Photo", color = Color.DarkGray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(officer.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(officer.email, fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Edit & Delete Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Delete")
                }
            }
        }
    }

    // ---------------- Delete Confirmation Dialog ----------------
    if (showDeleteDialog) {
        val context = LocalContext.current
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Officer") },
            text = { Text("Are you sure you want to delete this officer? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(officer)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    // ---------------- Edit Dialog ----------------
    if (showEditDialog) {
        AddCentralOfficerDialog(
            onDismiss = { showEditDialog = false },
            onAdd = { name, email, photoUri ->
                onEdit(CentralOfficer(name, email, photoUri))
                showEditDialog = false
            },
            initialData = officer
        )
    }
}




@Composable
fun AddCentralOfficerDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, email: String, photoUri: Uri?) -> Unit,
    initialData: CentralOfficer? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialData?.name ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(initialData?.email ?: "")) }
    var photoUri by remember { mutableStateOf<Uri?>(initialData?.photoUri) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> photoUri = uri }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column {
                Text(
                    text = if (initialData != null) "Edit Central Officer" else "Add Central Officer",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Photo picker
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.LightGray, shape = CircleShape)
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
                        Text("Upload Photo", color = Color.Black, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (name.text.isNotBlank() && email.text.isNotBlank()) onAdd(name.text, email.text, photoUri) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) { Text("Save", color = Color.White) }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
