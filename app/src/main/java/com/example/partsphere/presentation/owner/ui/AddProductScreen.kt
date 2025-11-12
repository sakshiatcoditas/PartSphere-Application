package com.example.partsphere.presentation.owner.ui

import android.R.attr.scaleX
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.presentation.owner.ui.manageemployee.ProductCard
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.ui.theme.White


data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: String,
    val description: String,
    val imageUrl: String? = null
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen() {
    var searchText by remember { mutableStateOf("") }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    // Full product list
    val fullProductList = listOf(
        Product(1, "Product A", "Electronics", "999", "Smart gadget for daily use", null),
        Product(2, "Product B", "Furniture", "2999", "Comfortable chair with modern design", null),
        Product(3, "Product C", "Sports", "499", "High-quality sports equipment", null),
        Product(4, "Product D", "Clothing", "899", "Stylish shirt for casual wear", null),
        Product(5, "Product E", "Electronics", "1599", "Wireless earphones", null)
    )

    // ✅ Derived State: filters + search combined automatically
    val filteredList by remember(searchText, selectedFilters) {
        derivedStateOf {
            fullProductList.filter { product ->
                // Match search text
                product.name.contains(searchText, ignoreCase = true) ||
                        product.description.contains(searchText, ignoreCase = true)
            }.filter { product ->
                // Match selected category filters
                selectedFilters.isEmpty() || product.category in selectedFilters
            }
        }
    }

    // --- UI below remains same ---
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

            // 🔹 Search Bar + Filter
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = searchText,
                    onQueryChange = { query -> searchText = query },
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
                        tint = Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Product List (real-time filtered)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { /* handle edit */ },
                        onDelete = { /* handle delete */ }
                    )
                }
            }
        }

        // Add Button
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

    // Add Product Dialog
    if (showAddProductDialog) {
        AddProductDialog(onDismiss = { showAddProductDialog = false })
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            selectedFilters = selectedFilters,
            onApply = { filters ->
                selectedFilters = filters
                showFilterDialog = false
            },
            onClear = {
                selectedFilters = emptySet()
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

//  Reusable function for consistent field color style
@Composable
fun outlinedFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Black,
    unfocusedBorderColor = Black,
    focusedLabelColor = Black,
    unfocusedLabelColor = Black,
    cursorColor = Black,
    focusedContainerColor = White,
    unfocusedContainerColor = White
)

//  FILTER DIALOG
@Composable
fun FilterDialog(
    selectedFilters: Set<String>,
    onApply: (Set<String>) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val allFilters = listOf("Electronics", "Furniture", "Clothing", "Sports")
    var selected by remember { mutableStateOf(selectedFilters.toMutableSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter by Category",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }

            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //  Clear Filters Button (always visible)
                TextButton(onClick = {
                    selected.clear()
                    onClear()
                }) {
                    Text("Clear Filters", color = Color.Black)
                }

                //  Apply Button
                Button(
                    onClick = { onApply(selected) },
                    colors = ButtonDefaults.buttonColors(containerColor = Black)
                ) {
                    Text("Apply", color = White)
                }
            }
        },
        containerColor = White,
        tonalElevation = 6.dp,
        shape = RoundedCornerShape(16.dp)
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onEdit: (Product) -> Unit,
    onDelete: (Product) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, tween(100))
    val overlayColor by animateColorAsState(if (isPressed) Color(0x22000000) else Color.Transparent, tween(150))

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

            // Top Row: Image + Info
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    if (product.imageUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(product.imageUrl),
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("No Image", color = Color.DarkGray, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Black
                    )
                    Text(
                        text = product.category,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹${product.price}",
                        fontSize = 15.sp,
                        color = Color(0xFF00897B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Short description
            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = { onEdit(product) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Black)
                ) {
                    Text("Edit")
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Black)
                ) {
                    Text("Delete")
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to delete this product? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(product)
                        showDeleteDialog = false
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
