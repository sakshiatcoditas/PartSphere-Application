package com.example.partsphere.presentation.owner.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.example.partsphere.R
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen() {
    var searchText by remember { mutableStateOf("") }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    val productList = listOf("Product A", "Product B", "Product C")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Add New Product",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Search Bar + Filter Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = searchText,
                    onQueryChange = { searchText = it },
                    placeholderText = "Search products",
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFFF2F2F2), shape = RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter",
                        tint = Black // you can remove tint if your vector already has color
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(productList) { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
                    ) {
                        Text(
                            text = product,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddProductDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Product", tint = White)
        }
    }

    // 🔹 ADD PRODUCT DIALOG
    if (showAddProductDialog) {
        AddProductDialog(onDismiss = { showAddProductDialog = false })
    }

    // 🔹 FILTER DIALOG
    if (showFilterDialog) {
        FilterDialog(
            selectedFilters = selectedFilters,
            onApply = { filters ->
                selectedFilters = filters
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductDialog(onDismiss: () -> Unit) {
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Electronics", "Furniture", "Clothing", "Sports")

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        photoUri = it
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Product",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // Product Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Product Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("Upload Image", color = Black, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Name
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        label = { Text("Category") },
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = outlinedFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = outlinedFieldColors()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Black)
            ) {
                Text("Done", color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Black)
            }
        },
        containerColor = White,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(16.dp)
    )
}

// 🔹 Reusable function for consistent field color style
@Composable
fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Black,
    unfocusedBorderColor = Color.LightGray,
    focusedLabelColor = Black,
    unfocusedLabelColor = Color.LightGray,
    cursorColor = Black,
    focusedContainerColor = White,
    unfocusedContainerColor = White
)

//  FILTER DIALOG
@Composable
fun FilterDialog(
    selectedFilters: Set<String>,
    onApply: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val allFilters = listOf("Electronics", "Furniture", "Clothing", "Sports")
    var selected by remember { mutableStateOf(selectedFilters.toMutableSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Filter by Category",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                allFilters.forEach { category ->
                    val isChecked = selected.contains(category)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable {
                                selected = if (isChecked) {
                                    selected.toMutableSet().apply { remove(category) }
                                } else {
                                    selected.toMutableSet().apply { add(category) }
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                selected = if (checked) {
                                    selected.toMutableSet().apply { add(category) }
                                } else {
                                    selected.toMutableSet().apply { remove(category) }
                                }
                            }
                        )
                        Text(category, fontSize = 16.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(selected) },
                colors = ButtonDefaults.buttonColors(containerColor = Black)
            ) {
                Text("Apply", color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Black)
            }
        },
        containerColor = White,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(16.dp)
    )
}

