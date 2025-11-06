package com.example.partsphere.presentation.owner.ui



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageFactoryScreen() {
    var searchText by remember { mutableStateOf("") }

    val factories = listOf(
        FactoryData("ABC Factory", "Mumbai", "John Doe"),
        FactoryData("XYZ Plant", "Pune", "Jane Smith"),
        FactoryData("LMN Factory", "Delhi", "Rahul Kumar")
    )

    val filteredFactories = factories.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.location.contains(searchText, ignoreCase = true) ||
                it.plantHead.contains(searchText, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = { Text("Manage Factory", color = Color.Black) },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFEDEDED))
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search factories") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredFactories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No results found", fontSize = 16.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredFactories) { factory ->
                    FactoryCard(factory)
                }
            }
        }
    }
}

@Composable
fun FactoryCard(factory: FactoryData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Factory Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(factory.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Location: ${factory.location}", fontSize = 14.sp, color = Color.Gray)
                    Text("Plant Head: ${factory.plantHead}", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO: Update action */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Update")
                }
                OutlinedButton(
                    onClick = { /* TODO: Delete action */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

data class FactoryData(
    val name: String,
    val location: String,
    val plantHead: String
)
