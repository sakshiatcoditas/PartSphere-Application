package com.example.partsphere.presentation.owner.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------- MAIN SCREEN ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFactoryScreen() {
    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }



    val factories = remember {
        mutableStateListOf(
            FactoryData("ABC Factory", "Mumbai", "John Doe"),
            FactoryData("XYZ Plant", "Pune", "Jane Smith"),
            FactoryData("LMN Factory", "Delhi", "Rahul Kumar")
        )
    }

    // Dummy locations (until API integration)
    val dummyLocations = listOf(
        "Mumbai", "Pune", "Delhi", "Chennai", "Bangalore",
        "Kolkata", "Hyderabad", "Ahmedabad", "Jaipur", "Lucknow"
    )

    val allLocations = (dummyLocations + factories.map { it.location }).distinct()
    var selectedLocations by remember { mutableStateOf(setOf<String>()) }

    val filteredFactories = factories.filter {
        val matchText = it.name.contains(searchText, ignoreCase = true) ||
                it.location.contains(searchText, ignoreCase = true) ||
                it.plantHead.contains(searchText, ignoreCase = true)
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

            //  Search + Filter Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Search factories") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
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

                //  Filter Button
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
                        imageVector = Icons.Default.Home, // Replace with FilterList icon if preferred
                        contentDescription = "Filter",
                        tint = if (selectedLocations.isNotEmpty()) Color.Black else Color(0xFF333333),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //  Factory List
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
                            onUpdate = { updated ->
                                val index = factories.indexOf(factory)
                                if (index != -1) factories[index] = updated
                            },
                            onDelete = { toDelete ->
                                factories.remove(toDelete) //   factory from list
                            }
                        )
                    }

                }
            }
        }

        // ➕ Floating Add Button
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

                        Button(
                            onClick = {
                                if (name.isNotBlank() && query.isNotBlank()) {
                                    factories.add(
                                        FactoryData(
                                            name = name,
                                            location = query,
                                            plantHead = "—"
                                        )
                                    )
                                    showAddDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
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
    factory: FactoryData,
    allLocations: List<String>,
    onUpdate: (FactoryData) -> Unit,
    onDelete: (FactoryData) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (isPressed) 0.85f else 1f, tween(100))
    val overlayColor by animateColorAsState(if (isPressed) Color(0x33000000) else Color.Transparent, tween(150))

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                    Text("Plant Head: ${factory.plantHead}", fontSize = 14.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                }
                            )
                        }
                        .background(overlayColor, RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text("Update", color = Color.White) }

                OutlinedButton(
                    onClick = {  onDelete(factory)  },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                ) { Text("Delete") }
            }
        }
    }

    // --- Editable Dialog ---
    // --- Editable Dialog ---
    if (showEditDialog) {
        var name by remember { mutableStateOf(factory.name) }
        var locationExpanded by remember { mutableStateOf(false) }
        var locationQuery by remember { mutableStateOf(factory.location) }

        var plantHeadExpanded by remember { mutableStateOf(false) }
        var plantHeadQuery by remember { mutableStateOf(factory.plantHead) }

        // Dummy data (later replace with API data)
        val allLocations = listOf("Mumbai", "Pune", "Delhi", "Bangalore", "Chennai", "Hyderabad")
        val allPlantHeads = listOf("Ravi Kumar", "Anjali Mehta", "Vikram Singh", "Sanya Patel", "Amit Shah")

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            confirmButton = {},
            text = {
                Column {
                    Text(
                        text = "Edit Factory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Factory Name field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Factory Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Searchable Location Dropdown ---
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
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
                            if (filteredLocations.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No match found") },
                                    onClick = {},
                                    enabled = false
                                )
                            } else {
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
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // --- Searchable Plant Head Dropdown ---
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
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
                            if (filteredPlantHeads.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No match found") },
                                    onClick = {},
                                    enabled = false
                                )
                            } else {
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
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancel", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onUpdate(
                                    factory.copy(
                                        name = name,
                                        location = locationQuery,
                                        plantHead = plantHeadQuery
                                    )
                                )
                                showEditDialog = false
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

// ---------------- DATA CLASS ----------------
data class FactoryData(
    val name: String,
    val location: String,
    val plantHead: String
)
