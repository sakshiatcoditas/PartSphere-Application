package com.example.partsphere.presentation.owner.ui
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.TextFieldDefaults


import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.partsphere.presentation.owner.navigation.OwnerBottomNavItem
import com.example.partsphere.presentation.owner.navigation.OwnerNavGraph





//-------------------- This has the Scaffold of the Homescreen just this -----------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDashboardScreen() {
    val navController = rememberNavController()
    val items = listOf(
        OwnerBottomNavItem.Home,
        OwnerBottomNavItem.Reports,
        OwnerBottomNavItem.ManageEmployee,
        OwnerBottomNavItem.ManageFactory,
        OwnerBottomNavItem.AddProduct,
        OwnerBottomNavItem.Profile
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner Dashboard", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { item ->

                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label, tint = Color.White) },
                        label = { Text(item.label, color = Color.White, fontSize = 10.sp) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            OwnerNavGraph(navController = navController)
        }
    }

}

//@Composable
//fun OwnerNavGraph(navController: NavHostController) {
//    NavHost(navController = navController, startDestination = OwnerBottomNavItem.Home.route) {
//        composable(OwnerBottomNavItem.Home.route) { OwnerHomeScreen() }
//        composable(OwnerBottomNavItem.Reports.route) { ReportsScreen() }
//        composable(OwnerBottomNavItem.ManageEmployee.route) { ManageEmployeeScreen() }
//        composable(OwnerBottomNavItem.ManageFactory.route) { ManageFactoryScreen() }
//        composable(OwnerBottomNavItem.Profile.route) { ProfileScreen() }
//    }
//}


//-------------------------Owner HomeScreen---------------------------------
//@Composable
//fun OwnerHomeScreen() {
//    // Make the screen scrollable
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(24.dp)
//    ) {
//        item {
//            Text(
//                text = "Business Overview",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//        }
//
//        item {
//            val employees = listOf(
//                "Workers" to 5000f,
//                "Chiefs" to 1200f,
//                "Plant Heads" to 300f
//            )
//
//            ComparisonBarChartCard(
//                title = "Employee Comparison",
//                data = employees,
//                barColor = Color(0xFF222222)
//            )
//        }
//
//        item {
//            val factoriesByLocation = listOf(
//                "Mumbai" to 10_000f,
//                "Pune" to 6_000f,
//                "Delhi" to 8_500f,
//                "Bangalore" to 3_500f
//            )
//
//            ComparisonBarChartCard(
//                title = "Factories by Location",
//                data = factoriesByLocation,
//                barColor = Color(0xFF222222)
//            )
//        }
//    }
//}
//
//@Composable
//fun ComparisonBarChartCard(
//    title: String,
//    data: List<Pair<String, Float>>,
//    barColor: Color
//) {
//    val maxValue = data.maxOfOrNull { it.second } ?: 1f
//    val maxBarHeight = 160f // adjust bar height if needed
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight(),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        ) {
//            Text(
//                text = title,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Bars area only
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(220.dp),
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                verticalAlignment = Alignment.Bottom
//            ) {
//                data.forEach { (label, value) ->
//                    val normalizedHeight = (value / maxValue) * maxBarHeight
//                    val animatedHeight by animateFloatAsState(
//                        targetValue = normalizedHeight,
//                        animationSpec = tween(durationMillis = 1000)
//                    )
//
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Bottom
//                    ) {
//                        // Show number above bar
//                        Text(
//                            text = formatNumber(value),
//                            fontSize = 12.sp,
//                            color = Color.Black
//                        )
//
//                        Spacer(modifier = Modifier.height(6.dp))
//
//                        // Bar itself
//                        Box(
//                            modifier = Modifier
//                                .height(animatedHeight.dp)
//                                .width(45.dp)
//                                .background(barColor, RoundedCornerShape(6.dp))
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // Label under bar
//                        Text(
//                            text = label,
//                            fontSize = 12.sp,
//                            color = Color.Black,
//                            textAlign = TextAlign.Center
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//
//
//fun formatNumber(value: Float): String {
//    return when {
//        value >= 1_000_000_000 -> String.format("%.1fB", value / 1_000_000_000) // billions
//        value >= 1_000_000 -> {
//            val formatted = value / 1_000_000
//            if (formatted % 1 == 0f) "${formatted.toInt()}M" else String.format("%.1fM", formatted)
//        }
//        value >= 1_000 -> {
//            val formatted = value / 1_000
//            if (formatted % 1 == 0f) "${formatted.toInt()}K" else String.format("%.1fK", formatted)
//        }
//        else -> value.toInt().toString()
//    }
//}










//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ManageFactoryScreen() {
//    var searchText by remember { mutableStateOf("") }
//
//    // Sample factory data
//    val factories = listOf(
//        FactoryData("ABC Factory", "Mumbai", "John Doe"),
//        FactoryData("XYZ Plant", "Pune", "Jane Smith"),
//        FactoryData("LMN Factory", "Delhi", "Rahul Kumar")
//    )
//
//    // Filter factories based on search text
//    val filteredFactories = factories.filter {
//        it.name.contains(searchText, ignoreCase = true) ||
//                it.location.contains(searchText, ignoreCase = true) ||
//                it.plantHead.contains(searchText, ignoreCase = true)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) {
//        // --- Top App Bar ---
//        TopAppBar(
//            title = { Text("Manage Factory", color = Color.Black) },
//            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEDEDED))
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // --- Search Bar ---
//        OutlinedTextField(
//            value = searchText,
//            onValueChange = { searchText = it },
//            placeholder = { Text("Search factories") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // --- Scrollable Factory Cards ---
//        if (filteredFactories.isEmpty()) {
//            // Show "No results found"
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("No results found", fontSize = 16.sp, color = Color.Gray)
//            }
//        } else {
//            // LazyColumn for scrollable cards
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(horizontal = 16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(filteredFactories) { factory ->
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .wrapContentHeight(),
//                        shape = RoundedCornerShape(12.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
//                    ) {
//                        Column(modifier = Modifier.padding(16.dp)) {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Icon(
//                                    imageVector = Icons.Default.Home,
//                                    contentDescription = "Factory Icon",
//                                    modifier = Modifier.size(40.dp),
//                                    tint = Color.Black
//                                )
//                                Spacer(modifier = Modifier.width(12.dp))
//                                Column {
//                                    Text(factory.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
//                                    Text("Location: ${factory.location}", fontSize = 14.sp, color = Color.Gray)
//                                    Text("Plant Head: ${factory.plantHead}", fontSize = 14.sp, color = Color.Gray)
//                                }
//                            }
//
//                            Spacer(modifier = Modifier.height(12.dp))
//
//                            Row(
//                                modifier = Modifier.fillMaxWidth(),
//                                horizontalArrangement = Arrangement.spacedBy(16.dp)
//                            ) {
//                                Button(
//                                    onClick = { /* TODO: Update action */ },
//                                    modifier = Modifier.weight(1f)
//                                ) {
//                                    Text("Update")
//                                }
//                                OutlinedButton(
//                                    onClick = { /* TODO: Delete action */ },
//                                    modifier = Modifier.weight(1f)
//                                ) {
//                                    Text("Delete")
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Data class for factories
//data class FactoryData(
//    val name: String,
//    val location: String,
//    val plantHead: String
//)
//





