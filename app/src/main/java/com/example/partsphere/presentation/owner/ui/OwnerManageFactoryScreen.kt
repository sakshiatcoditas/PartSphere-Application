package com.example.partsphere.presentation.owner.ui

import androidx.compose.ui.res.painterResource
import com.example.partsphere.R

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import android.widget.Toast
import androidx.compose.foundation.clickable
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
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.presentation.owner.viewmodel.ManageFactoryViewModel

// ---------------- MAIN SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFactoryScreen(viewModel: ManageFactoryViewModel = hiltViewModel()) {
    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val factoriesState by viewModel.factories.collectAsState()

    val factories = factoriesState.map { apiItem ->
        FactoryItem(
            id = apiItem.id,
            name = apiItem.name,
            location = apiItem.location,
            plantheadName = apiItem.plantheadName ?: "—"
        )
    }

    LaunchedEffect(Unit) {
        viewModel.fetchFactories()
    }

    // Locations for filtering dropdown
    val allLocations = (factories.map { it.location } + listOf(
        "Mumbai", "Pune", "Delhi", "Chennai", "Bangalore",
        "Kolkata", "Hyderabad", "Ahmedabad", "Jaipur", "Lucknow"
    )).distinct()

    var selectedLocations by remember { mutableStateOf(setOf<String>()) }

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
            Text(
                text = "All Factories",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // --- Search + Filter Row ---
            Row(verticalAlignment = Alignment.CenterVertically) {

                SearchBar(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    placeholderText = "Search factories",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
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
                        painter = painterResource(id = R.drawable.filter),
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
                                    viewModel = viewModel
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

    // -------------------- Filter Dialog --------------------
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

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var nameError by remember { mutableStateOf<String?>(null) }
        var location by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var query by remember { mutableStateOf("") }

        val context = LocalContext.current

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
                        onValueChange = {
                            name = it
                            val regex = "^[A-Za-z]+( [A-Za-z]+)*$".toRegex()
                            nameError = if (it.isBlank() || regex.matches(it)) null else "Only letters are allowed"
                        },
                        label = { Text("Factory Name") },
                        isError = nameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

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

                        Button(
                            onClick = {
                                when {
                                    name.isBlank() || query.isBlank() -> {
                                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }
                                    nameError != null -> return@Button
                                    else -> {
                                        viewModel.createFactory(name, query) { success, message ->
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            if (success) {
                                                showAddDialog = false
                                                viewModel.fetchFactories()
                                            }
                                        }
                                    }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FactoryCard(
    factory: FactoryItem,
    allLocations: List<String>,
    viewModel: ManageFactoryViewModel
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(factory.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Location: ${factory.location}", fontSize = 14.sp, color = Color.DarkGray)
                    Text("Plant Head: ${factory.plantheadName ?: "—"}", fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { showEditDialog = true },
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

    val context = LocalContext.current

    if (showEditDialog) {
        val unassignedHeads by viewModel.unassignedPlantHeads.collectAsState()
        val updateMessage by viewModel.updateMessage.collectAsState()

        var name by remember { mutableStateOf(factory.name) }
        var nameError by remember { mutableStateOf<String?>(null) }

        var location by remember { mutableStateOf(factory.location) }
        var locationExpanded by remember { mutableStateOf(false) }

        var selectedPlantHead by remember { mutableStateOf(factory.plantheadName ?: "") }
        var selectedPlantHeadId by remember { mutableStateOf<Int?>(null) }
        var plantHeadExpanded by remember { mutableStateOf(false) }

        LaunchedEffect(updateMessage) {
            updateMessage?.let { msg ->
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                if (msg.contains("success", ignoreCase = true)) {
                    showEditDialog = false
                    viewModel.fetchFactories()
                }
                viewModel.clearUpdateMessage()
            }
        }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {},
            text = {
                Column {
                    Text("Edit Factory", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    // --- Name field with regex validation ---
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            val regex = "^[A-Za-z]+( [A-Za-z]+)*$".toRegex()
                            nameError = if (it.isBlank() || regex.matches(it)) null else "Only letters and spaces allowed"
                        },
                        label = { Text("Factory Name") },
                        isError = nameError != null,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (nameError != null) {
                        Text(
                            text = nameError!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Location Dropdown ---
                    ExposedDropdownMenuBox(
                        expanded = locationExpanded,
                        onExpandedChange = { locationExpanded = !locationExpanded }
                    ) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                locationExpanded = true
                            },
                            label = { Text("Location") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(locationExpanded) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        val filteredLocations = allLocations.filter {
                            it.contains(location, ignoreCase = true)
                        }

                        ExposedDropdownMenu(
                            expanded = locationExpanded,
                            onDismissRequest = { locationExpanded = false }
                        ) {
                            filteredLocations.forEach { loc ->
                                DropdownMenuItem(
                                    text = { Text(loc) },
                                    onClick = {
                                        location = loc
                                        locationExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Plant Head Dropdown ---
                    ExposedDropdownMenuBox(
                        expanded = plantHeadExpanded,
                        onExpandedChange = { expanded ->
                            plantHeadExpanded = expanded
                            if (expanded) viewModel.fetchUnassignedPlantHeads()
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedPlantHead,
                            onValueChange = {},
                            label = { Text("Plant Head") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(plantHeadExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                                .fillMaxWidth()
                                .clickable {
                                    plantHeadExpanded = !plantHeadExpanded
                                    if (plantHeadExpanded) viewModel.fetchUnassignedPlantHeads()
                                }
                        )

                        val filteredHeads = remember(unassignedHeads, selectedPlantHead) {
                            if (selectedPlantHead.isBlank()) unassignedHeads
                            else unassignedHeads.filter {
                                it.username.contains(selectedPlantHead, ignoreCase = true)
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = plantHeadExpanded,
                            onDismissRequest = { plantHeadExpanded = false }
                        ) {
                            if (filteredHeads.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No available plant heads") },
                                    onClick = {},
                                    enabled = false
                                )
                            } else {
                                filteredHeads.forEach { head ->
                                    DropdownMenuItem(
                                        text = { Text(head.username) },
                                        onClick = {
                                            selectedPlantHead = head.username
                                            selectedPlantHeadId = head.id
                                            plantHeadExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Update + Cancel buttons ---
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (nameError != null) return@Button
                                val req = com.example.partsphere.presentation.owner.model.UpdateFactoryRequest(
                                    name = name,
                                    location = location,
                                    plantHead_id = selectedPlantHeadId
                                )
                                viewModel.updateFactory(factory.id, req)
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Factory") },
            text = { Text("Are you sure you want to delete this factory?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFactory(factory.id) { success, message ->
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            if (success) viewModel.fetchFactories()
                            showDeleteDialog = false
                        }
                    }
                ) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}



