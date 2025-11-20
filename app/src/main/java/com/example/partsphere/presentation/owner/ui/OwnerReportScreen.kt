package com.example.partsphere.presentation.owner.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.res.painterResource
import com.example.partsphere.R

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------- DATA ------------------------

data class FactoryReport(
    val highestProduct: String,
    val mostUsedTool: String,
    val leastUsedTool: String
)

fun getFactoryReport(factory: String): FactoryReport {
    return when (factory) {
        "Factory A" -> FactoryReport("Steel Rods", "Hydraulic Press", "Lathe Machine")
        "Factory B" -> FactoryReport("Copper Wires", "Welding Machine", "Hammer Drill")
        "Factory C" -> FactoryReport("Bolts & Nuts", "Cutter", "Shearing Tool")
        "Factory D" -> FactoryReport("Aluminium Sheets", "CNC Machine", "Manual Grinder")
        else -> FactoryReport("-", "-", "-")
    }
}

val factoryLocations = mapOf(
    "Factory A" to "Delhi",
    "Factory B" to "Mumbai",
    "Factory C" to "Chennai",
    "Factory D" to "Bangalore"
)

val allLocations: List<String> by lazy {
    factoryLocations.values.toSet().sorted()
}

// --------------------- SCREEN ------------------------

@Composable
fun ReportsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }
    var selectedFactory by remember { mutableStateOf<String?>(null) }
    var isDropdownOpen by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var appliedLocations by remember { mutableStateOf<Set<String>>(emptySet()) }
    var reportData by remember { mutableStateOf(FactoryReport("-", "-", "-")) }

    val allFactories = remember { factoryLocations.keys.sorted() }
    val scope = rememberCoroutineScope()

    // Debounced search effect
    LaunchedEffect(searchQuery) {
        scope.launch {
            delay(300)
            debouncedQuery = searchQuery
        }
    }

    // Filter factories by location and search
    val filteredFactories = remember(appliedLocations, debouncedQuery) {
        allFactories
            .filter { factory ->
                if (appliedLocations.isEmpty()) true
                else factoryLocations[factory] in appliedLocations
            }
            .filter { factory ->
                if (debouncedQuery.isBlank()) true
                else factory.contains(debouncedQuery, ignoreCase = true)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        ReportsTopBar()

        Spacer(modifier = Modifier.height(16.dp))

        SearchAndFilterRow(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                if (it.isNotEmpty()) isDropdownOpen = true
            },
            onClearSearch = {
                searchQuery = ""
                isDropdownOpen = false
            },
            onFilterClick = { showFilterDialog = true },
            hasActiveFilters = appliedLocations.isNotEmpty(),
            isDropdownOpen = isDropdownOpen,
            onDropdownDismiss = { isDropdownOpen = false },
            filteredFactories = filteredFactories,
            onFactorySelected = { factory ->
                selectedFactory = factory
                searchQuery = factory
                isDropdownOpen = false
                reportData = getFactoryReport(factory)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FactorySelectorCard(factoryName = selectedFactory ?: "Select Factory")

        Spacer(modifier = Modifier.height(24.dp))

        ReportCardsSection(reportData)
    }

    if (showFilterDialog) {
        LocationFilterDialog(
            locations = allLocations,
            selectedLocations = appliedLocations,
            onApply = { selected ->
                appliedLocations = selected
                showFilterDialog = false

                // Reset factory if it doesn't match new filters
                selectedFactory?.let { factory ->
                    val factoryLocation = factoryLocations[factory]
                    if (selected.isNotEmpty() && factoryLocation !in selected) {
                        selectedFactory = null
                        searchQuery = ""
                        reportData = FactoryReport("-", "-", "-")
                    }
                }
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

// ---------------- COMPONENTS --------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Reports",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}

@Composable
fun SearchAndFilterRow(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilters: Boolean,
    isDropdownOpen: Boolean,
    onDropdownDismiss: () -> Unit,
    filteredFactories: List<String>,
    onFactorySelected: (String) -> Unit
) {
    var searchBarWidth by remember { mutableStateOf(0) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text("Search factories...", fontSize = 14.sp, color = Color.Gray)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .onGloballyPositioned { coords ->
                        searchBarWidth = coords.size.width
                    },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFDDDDDD),
                    focusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = onFilterClick,
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color.White, // White background
                        shape = RoundedCornerShape(14.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.filter), // Use your filter drawable
                    contentDescription = "Filter by location",
                    tint = Color.Black, // Icon remains black
                    modifier = Modifier.size(24.dp)
                )
            }


        }

        DropdownMenu(
            expanded = isDropdownOpen,
            onDismissRequest = onDropdownDismiss,
            modifier = Modifier.width(
                with(LocalDensity.current) { searchBarWidth.toDp() }
            )
        ) {
            if (filteredFactories.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "No factories found",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    onClick = { }
                )
            } else {
                filteredFactories.forEach { factory ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = factory,
                                fontSize = 15.sp
                            )
                        },
                        onClick = { onFactorySelected(factory) }
                    )
                }
            }
        }
    }
}

@Composable
fun FactorySelectorCard(factoryName: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = factoryName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (factoryName == "Select Factory") Color.Gray else Color.Black
            )
        }
    }
}

@Composable
fun ReportCardsSection(report: FactoryReport) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReportCard(
            title = "Highest Manufactured Product",
            value = report.highestProduct,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ReportCard(
                title = "Most Used Tool",
                value = report.mostUsedTool,
                modifier = Modifier.weight(1f)
            )

            ReportCard(
                title = "Least Used Tool",
                value = report.leastUsedTool,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ReportCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF666666),
                lineHeight = 16.sp
            )

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun LocationFilterDialog(
    locations: List<String>,
    selectedLocations: Set<String>,
    onApply: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var tempSelected by remember { mutableStateOf(selectedLocations.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filter by Location",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                locations.forEach { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tempSelected = if (location in tempSelected) {
                                    tempSelected - location
                                } else {
                                    tempSelected + location
                                }
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = location in tempSelected,
                            onCheckedChange = { checked ->
                                tempSelected = if (checked) {
                                    tempSelected + location
                                } else {
                                    tempSelected - location
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = location,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(tempSelected) }) {
                Text(
                    text = "Apply",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        }
    )
}