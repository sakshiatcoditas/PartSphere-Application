package com.example.partsphere.presentation.owner.ui


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.partsphere.presentation.owner.viewmodel.OwnerHomeViewModel

@Composable
fun OwnerHomeScreen(viewModel: OwnerHomeViewModel = hiltViewModel()) {

    val employeeCounts = viewModel.employeeCounts
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage

    val factoryLocations by viewModel.factoryLocations
    val isFactoryLoading by viewModel.isFactoryLoading
    val factoryErrorMessage by viewModel.factoryErrorMessage

    LaunchedEffect(Unit) {
        viewModel.fetchEmployeeCounts()
        viewModel.fetchFactoryLocations()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Business Overview",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        item {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (errorMessage != null) {
                Text("Error: $errorMessage", color = Color.Red)
            } else if (employeeCounts.isEmpty()) {
                Text("No employee data available", color = Color.Gray)
            } else {
                val employees = employeeCounts.map { it.role to it.count.toFloat() }

                ComparisonBarChartCard(
                    title = "Employee Comparison",
                    data = employees,
                    barColor = Color(0xFF222222)
                )
            }
        }


        item {
            if (isFactoryLoading) {
                CircularProgressIndicator()
            } else if (factoryErrorMessage != null) {
                Text("Error: $factoryErrorMessage", color = Color.Red)
            } else if (factoryLocations.isEmpty()) {
                Text("No factory data available", color = Color.Gray)
            } else {
                val factories = factoryLocations.map { it.location to it.factoryCount.toFloat() }
                ComparisonBarChartCard(
                    title = "Factories by Location",
                    data = factories,
                    barColor = Color(0xFF222222)
                )
            }
        }
    }
}

@Composable
fun ComparisonBarChartCard(
    title: String,
    data: List<Pair<String, Float>>,
    barColor: Color
) {
    val maxValue = data.maxOfOrNull { it.second } ?: 1f
    val maxBarHeight = 160f
    val barWidth = 40.dp
    val horizontalPadding = 12.dp
    val valueBoxHeight = 20.dp // space for count value above bar

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(horizontalPadding)
            ) {
                data.forEach { (label, value) ->
                    val normalizedHeight = (value / maxValue) * maxBarHeight
                    val animatedHeight by animateFloatAsState(
                        targetValue = normalizedHeight,
                        animationSpec = tween(durationMillis = 1000)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Fixed-height Box to hold bar + value
                        Box(
                            modifier = Modifier
                                .height(maxBarHeight.dp + valueBoxHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            // Bar
                            Box(
                                modifier = Modifier
                                    .width(barWidth)
                                    .height(animatedHeight.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(barColor, RoundedCornerShape(6.dp))
                            )

                            // Value above the bar, but inside Box
                            Text(
                                text = formatNumber(value),
                                fontSize = 12.sp,
                                color = Color.Black,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = -(animatedHeight + 4).dp) // position just above bar
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Label below bar, wrap naturally, reduce line spacing
                        Text(
                            text = label.replace("_", "\n"),
                            fontSize = 10.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun formatNumber(value: Float): String {
    return when {
        value >= 1_000_000 -> String.format("%.1fM", value / 1_000_000)
        value >= 1_000 -> String.format("%.1fK", value / 1_000)
        else -> value.toInt().toString()
    }
}
