package com.example.partsphere.presentation.owner.ui.manageemployee

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.presentation.owner.viewmodel.ManageFactoryViewModel

// --- Data model ---
data class CentralOfficer(
    val id:Int,
    val name: String,
    val email: String,
    val photoUrl: String? = null,   // for fetched image (Cloudinary URL)
    val localPhotoUri: Uri? = null  // for temporary local image before upload
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralOfficerScreen(
    onBackClick: () -> Unit,
    viewModel: ManageFactoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var officerToEdit by remember { mutableStateOf<CentralOfficer?>(null) }

    //  Fetch officers when the screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchCentralOfficers()
    }

    //  Search state
    var searchQuery by remember { mutableStateOf("") }

    //  Filter officers by search query
    val filteredOfficers = uiState.officers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Central Officers", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    officerToEdit = null
                    showDialog = true
                },
                containerColor = Color.Black,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Officer", tint = Color.White)
            }
        }
    ) { innerPadding ->

        //  Use the Scaffold’s padding directly
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            //  Search Bar (fixed top)
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Search Central Officers",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 100.dp //  ensures list can scroll behind FAB & bottom nav
                )
            ) {
                if (filteredOfficers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(top = 100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Central Officers found", fontSize = 16.sp, color = Color.Gray)
                        }
                    }
                } else {
                    items(filteredOfficers) { officer ->
                        CentralOfficerCard(
                            officer = officer,
                            onDelete = { officerToDelete -> viewModel.deleteCentralOfficer(officerToDelete.id) },
                            viewModel = viewModel
                        )
                    }

                }
            }
        }


        //  loading overlay
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        // Error toast
        uiState.error?.let { error ->
            LaunchedEffect(error) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }

        // Dialog
        if (showDialog) {
            AddCentralOfficerDialog(
                onDismiss = { showDialog = false },
                onAdd = { name, email, photoUri, _ ->   // 4th param ignored for new add
                    viewModel.addCentralOfficer(name, email, photoUri)
                    showDialog = false
                },
                initialData = officerToEdit
            )
        }

    }

    // loading overlay
    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }




}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CentralOfficerCard(
    officer: CentralOfficer,

    onDelete: (CentralOfficer) -> Unit,
    viewModel: ManageFactoryViewModel
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
                    val painter = rememberAsyncImagePainter(
                        officer.localPhotoUri ?: officer.photoUrl
                    )

                    if (officer.localPhotoUri != null || officer.photoUrl != null) {
                        Image(
                            painter = painter,
                            contentDescription = officer.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("No Photo", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }



                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(officer.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(officer.email, fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                ) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    if (showEditDialog) {
        val context = LocalContext.current

        AddCentralOfficerDialog(
            onDismiss = { showEditDialog = false },
            onAdd = { name, email, photoUri, _ ->
                val officerId = officer.id

                viewModel.updateCentralOfficer(
                    context = context,
                    officerId = officerId,
                    username = name,
                    email = email,
                    photoUri = photoUri
                ) { success ->
                    if (success) {
                        Toast.makeText(context, "Edited successfully ", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    } else {
                        Toast.makeText(context, "Edit failed. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            initialData = officer
        )
    }

}

@Composable
fun AddCentralOfficerDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, email: String, photoUri: Uri?, existingPhotoUrl: String?) -> Unit,
    initialData: CentralOfficer? = null
) {
    var name by remember { mutableStateOf(TextFieldValue(initialData?.name ?: "")) }
    var email by remember { mutableStateOf(TextFieldValue(initialData?.email ?: "")) }
    var photoUri by remember { mutableStateOf<Uri?>(initialData?.localPhotoUri) }

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

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(Color.LightGray, shape = CircleShape)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        photoUri != null -> { // user picked new photo
                            Image(
                                painter = rememberAsyncImagePainter(photoUri),
                                contentDescription = "Selected Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        initialData?.photoUrl != null -> { // existing photo from backend
                            Image(
                                painter = rememberAsyncImagePainter(initialData.photoUrl),
                                contentDescription = "Existing Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        else -> {
                            Text("Upload Photo", color = Color.Black, fontSize = 14.sp)
                        }
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
                        onClick = {
                            if (name.text.isNotBlank() && email.text.isNotBlank()) {
                                // Pass both photoUri (new photo) and existing photo URL
                                onAdd(name.text, email.text, photoUri, initialData?.photoUrl)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) { Text("Save", color = Color.White) }
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}
