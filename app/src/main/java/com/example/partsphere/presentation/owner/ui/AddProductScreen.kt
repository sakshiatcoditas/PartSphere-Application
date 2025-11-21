package com.example.partsphere.presentation.owner.ui

import android.R.attr.scaleX
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.partsphere.presentation.owner.model.Product
import com.example.partsphere.presentation.owner.ui.components.SearchBar
import com.example.partsphere.presentation.owner.viewmodel.ManageFactoryViewModel
import com.example.partsphere.ui.theme.Black
import com.example.partsphere.ui.theme.White




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: ManageFactoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.productUiState.collectAsState()
    val context = LocalContext.current

    //  Load first page
    LaunchedEffect(Unit) {
        viewModel.fetchProducts(page = 0)
    }

    var searchText by remember { mutableStateOf("") }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }

    val listState = rememberLazyListState()


    LaunchedEffect(listState, uiState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItems = uiState.products.size
                val totalPages = uiState.totalPages
                val currentPage = uiState.currentPage

                if (lastVisibleItemIndex == totalItems - 1 &&
                    !uiState.isLoading &&
                    currentPage + 1 < totalPages
                ) {
                    viewModel.fetchProducts(page = currentPage + 1, isNextPage = true)
                }
            }
    }


    val filteredList by remember(uiState.products, searchText, selectedFilters) {
        derivedStateOf {
            uiState.products.filter { product ->
                (product.name.contains(searchText, ignoreCase = true) ||
                        product.description.contains(searchText, ignoreCase = true))
                        && (selectedFilters.isEmpty() || product.categoryName in selectedFilters)
            }
        }
    }


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
                text = "All Products",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "Filter",
                        tint = Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredList, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { /* TODO: handle edit */ },
                        onDelete = { productToDelete ->
                            viewModel.deleteProduct(productToDelete.id) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    viewModel.fetchProducts(page = 0) // refresh first page
                                }
                            }
                        }
                    )
                }

                if (uiState.isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Black)
                        }
                    }
                }
            }

        }

        //  Floating Add button
        FloatingActionButton(
            onClick = { showAddProductDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Black
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Product", tint = White)
        }

        if (showAddProductDialog) {
            AddProductDialog(viewModel = viewModel, onDismiss = { showAddProductDialog = false })
        }



        uiState.error?.let { errorMsg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xAA000000)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: $errorMsg", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.fetchProducts(uiState.currentPage) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }

    //  Dialogs
    if (showAddProductDialog) {
        AddProductDialog(onDismiss = { showAddProductDialog = false }, viewModel = viewModel)
    }

    if (showFilterDialog) {
        FilterDialog(
            selectedFilters = selectedFilters,
            onApply = {
                selectedFilters = it
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
fun AddProductDialog(
    viewModel: ManageFactoryViewModel,
    onDismiss: () -> Unit
) {
    var productName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf(0) }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Electronics", "Furniture", "Clothing", "Sports")

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { photoUri = it }
    )

    var productNameError by remember { mutableStateOf(false) }
    var quantityError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // Image Picker
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
                    onValueChange = {
                        productName = it
                        productNameError = it.isBlank() || !it.matches(Regex("^[a-zA-Z\\s]*$"))
                    },
                    label = { Text("Product Name") },
                    isError = productNameError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors()
                )
                if (productNameError) {
                    Text("Product Name must contain letters only", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        quantity = it
                        quantityError = it.isBlank() || !it.matches(Regex("^[0-9]*$"))
                    },
                    label = { Text("Quantity") },
                    isError = quantityError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors()
                )
                if (quantityError) {
                    Text("Quantity must be a number", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = if (categoryId == 0) "" else categories[categoryId - 1],
                        onValueChange = {},
                        label = { Text("Category") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = outlinedFieldColors()
                    )

                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        categories.forEachIndexed { index, option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = {
                                categoryId = index + 1
                                categoryExpanded = false
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                OutlinedTextField(
                    value = price,
                    onValueChange = {
                        price = it
                        priceError = it.isBlank() || !it.matches(Regex("^[0-9]*\\.?[0-9]*$"))
                    },
                    label = { Text("Price") },
                    isError = priceError,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedFieldColors()
                )
                if (priceError) {
                    Text("Price must be a valid number", color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        descriptionError = it.isBlank() || !it.matches(Regex("^[a-zA-Z\\s]*$"))
                    },
                    label = { Text("Description") },
                    isError = descriptionError,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = outlinedFieldColors()
                )
                if (descriptionError) {
                    Text("Description must contain letters only", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    productNameError = productName.isBlank() || !productName.matches(Regex("^[a-zA-Z\\s]*$"))
                    quantityError = quantity.isBlank() || !quantity.matches(Regex("^[0-9]*$"))
                    priceError = price.isBlank() || !price.matches(Regex("^[0-9]*\\.?[0-9]*$"))
                    descriptionError = description.isBlank() || !description.matches(Regex("^[a-zA-Z\\s]*$"))

                    if (!productNameError && !quantityError && !priceError && !descriptionError && categoryId != 0) {
                        viewModel.createProduct(
                            name = productName,
                            imageUri = photoUri,
                            quantity = quantity.toInt(),
                            categoryId = categoryId,
                            price = price.toDouble(),
                            description = description
                        )
                        Toast.makeText(context, "Product Created!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    } else {
                        Toast.makeText(context, "Please correct the errors", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Black)
            ) {
                Text("Done", color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Black) }
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
                detectTapGestures(onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                })
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Image + info row
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
                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Black)
                    Text(product.categoryName, fontSize = 14.sp, color = Color.Gray)
                    Text("₹${product.price}", fontSize = 15.sp, color = Color(0xFF00897B), fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(product.description, fontSize = 14.sp, color = Color.DarkGray, maxLines = 2)

            Spacer(modifier = Modifier.height(16.dp))

            // Edit & Delete buttons
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { onEdit(product) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = Black)) {
                    Text("Edit")
                }

                OutlinedButton(onClick = { showDeleteDialog = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = Black)) {
                    Text("Delete")
                }
            }
        }
    }

    // Delete confirmation
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to delete this product? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(product)
                    showDeleteDialog = false
                }) {
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
}

