package com.example.partsphere.presentation.owner.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.partsphere.presentation.owner.model.FactoryItem
import com.example.partsphere.presentation.owner.viewmodel.ManageFactoryViewModel

// ---------------- MAIN SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFactoryScreen(viewModel: ManageFactoryViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // --- Collect state from ViewModel ---
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    // Mutable list for UI updates (update/delete)

    val factoriesState by viewModel.factories.collectAsState()

    //  Step 2: Map them into your UI model (FactoryItem)
    val factories = factoriesState.map { apiItem ->
        FactoryItem(
            id = apiItem.id,
            name = apiItem.name,
            location = apiItem.location,
            plantheadName = apiItem.plantheadName ?: "—"
        )
    }

    // Step 3: Fetch data once when screen opens
    LaunchedEffect(Unit) {
        viewModel.fetchFactories()
    }

    // Locations for filtering dropdown (from API + some default)
    val allLocations = (factories.map { it.location } + listOf(
        "Mumbai", "Pune", "Delhi", "Chennai", "Bangalore",
        "Kolkata", "Hyderabad", "Ahmedabad", "Jaipur", "Lucknow"
    )).distinct()

    var selectedLocations by remember { mutableStateOf(setOf<String>()) }

    // Filter factories based on search text & selected locations
    val filteredFactories = factories.filter {
        val matchText = it.name.contains(searchText, ignoreCase = true) ||
                it.location.contains(searchText, ignoreCase = true) ||
                (it.plantheadName ?: "").contains(searchText, ignoreCase = true)
        val matchLocation = selectedLocations.isEmpty() || it.location in selectedLocations
        matchText && matchLocation
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Search + Filter Row ---
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search factories") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Gray,
                        focusedBorderColor = Color.Black,
                        unfocusedContainerColor = Color(0xFFF8F8F8),
                        focusedContainerColor = Color(0xFFF8F8F8)
                    )
                )

                // Filter Button
                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(6.dp, RoundedCornerShape(14.dp))
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .border(
                            width = 1.dp,
                            color = if (selectedLocations.isNotEmpty()) Color.Black else Color(0xFFDDDDDD),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Filter",
                        tint = if (selectedLocations.isNotEmpty()) Color.Black else Color(0xFF333333),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- Loading / Error / Factory List ---
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = Color.Red)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (filteredFactories.isEmpty()) {
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
                            items(filteredFactories) { factory ->
                                FactoryCard(
                                    factory = factory,
                                    allLocations = allLocations,

                                    onDelete = {
                                        viewModel.fetchFactories() // refresh the data after delete
                                               },
                                    viewModel = viewModel // pass it here
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Floating Add Button ---
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Color.Black,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Factory", tint = Color.White)
        }
    }

    //  FILTER DIALOG
    if (showFilterDialog) {
        var query by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            confirmButton = {},
            text = {
                Column {
                    Text(
                        text = "Filter by Location",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Search Field
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Search location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color.Gray,
                            focusedBorderColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val filteredLocations = allLocations.filter { it.contains(query, ignoreCase = true) }

                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 200.dp)
                            .fillMaxWidth()
                    ) {
                        items(filteredLocations) { loc ->
                            val isSelected = loc in selectedLocations
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures {
                                            selectedLocations =
                                                if (isSelected) selectedLocations - loc
                                                else selectedLocations + loc
                                        }
                                    }
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        selectedLocations =
                                            if (it) selectedLocations + loc
                                            else selectedLocations - loc
                                    }
                                )
                                Text(loc, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = {
                            selectedLocations = emptySet()
                            showFilterDialog = false
                        }) {
                            Text("Clear", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { showFilterDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Apply", color = Color.White)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    //  ADD FACTORY DIALOG
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var query by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            confirmButton = {},
            text = {
                Column {
                    Text(
                        text = "Create New Factory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Factory Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Searchable Location Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = query,
                            onValueChange = {
                                query = it
                                expanded = true
                            },
                            label = { Text("Factory Location") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        )

                        val filtered = allLocations.filter { it.contains(query, ignoreCase = true) }

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            if (filtered.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No match found") },
                                    onClick = {},
                                    enabled = false
                                )
                            } else {
                                filtered.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc) },
                                        onClick = {
                                            query = loc
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        val context = LocalContext.current

                        Button(
                            onClick = {
                                if (name.isNotBlank() && query.isNotBlank()) {
                                    viewModel.createFactory(name, query) { success, message ->
                                        if (success) {
                                            showAddDialog = false
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            viewModel.fetchFactories() // optional refresh if you added this method
                                        } else {
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Create Factory", color = Color.White)
                        }


                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}


// ---------------- FACTORY CARD ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactoryCard(
    factory: FactoryItem,
    allLocations: List<String>,
    onDelete: (FactoryItem) -> Unit,
    viewModel: ManageFactoryViewModel
) {
    var isPressed by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (isPressed) 0.85f else 1f, tween(100))
    val overlayColor by animateColorAsState(if (isPressed) Color(0x33000000) else Color.Transparent, tween(150))

// ---------------- Factory Card UI ----------------
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(overlayColor),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(factory.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Location: ${factory.location}", fontSize = 14.sp, color = Color.DarkGray)
                    Text("Plant Head: ${factory.plantheadName ?: "—"}", fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- Edit & Delete Buttons ----------------
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showEditDialog = true }, // Opens the edit dialog
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = { onDelete(factory) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) {
                    Text("Delete")
                }
            }
        }
    }

// ---------------- Edit Dialog ----------------
    if (showEditDialog) {
        var name by remember { mutableStateOf(factory.name) }
        var locationExpanded by remember { mutableStateOf(false) }
        var locationQuery by remember { mutableStateOf(factory.location) }

        var plantHeadExpanded by remember { mutableStateOf(false) }
        var plantHeadQuery by remember { mutableStateOf(factory.plantheadName ?: "") }

        val allPlantHeads = listOf("Ravi Kumar", "Anjali Mehta", "Vikram Singh", "Sanya Patel", "Amit Shah")

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {},
            text = {
                Column {
                    Text("Edit Factory", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                    // Factory Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Factory Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Location Dropdown
                    ExposedDropdownMenuBox(
                        expanded = locationExpanded,
                        onExpandedChange = { locationExpanded = !locationExpanded }
                    ) {
                        OutlinedTextField(
                            value = locationQuery,
                            onValueChange = {
                                locationQuery = it
                                locationExpanded = true
                            },
                            label = { Text("Location") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(locationExpanded) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        val filteredLocations = allLocations.filter { it.contains(locationQuery, ignoreCase = true) }

                        ExposedDropdownMenu(
                            expanded = locationExpanded,
                            onDismissRequest = { locationExpanded = false }
                        ) {
                            filteredLocations.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc) },
                                    onClick = {
                                        locationQuery = loc
                                        locationExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Plant Head Dropdown
                    ExposedDropdownMenuBox(
                        expanded = plantHeadExpanded,
                        onExpandedChange = { plantHeadExpanded = !plantHeadExpanded }
                    ) {
                        OutlinedTextField(
                            value = plantHeadQuery,
                            onValueChange = {
                                plantHeadQuery = it
                                plantHeadExpanded = true
                            },
                            label = { Text("Plant Head Name") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(plantHeadExpanded) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Black,
                                unfocusedBorderColor = Color.Gray
                            )
                        )

                        val filteredPlantHeads = allPlantHeads.filter { it.contains(plantHeadQuery, ignoreCase = true) }

                        ExposedDropdownMenu(
                            expanded = plantHeadExpanded,
                            onDismissRequest = { plantHeadExpanded = false }
                        ) {
                            filteredPlantHeads.forEach { head ->
                                DropdownMenuItem(
                                    text = { Text(head) },
                                    onClick = {
                                        plantHeadQuery = head
                                        plantHeadExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Update + Cancel buttons
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showEditDialog = false }) { Text("Cancel", color = Color.Gray) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.updateFactory(factory.id, name) { success ->
                                    if (success) showEditDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Update", color = Color.White)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }


}


