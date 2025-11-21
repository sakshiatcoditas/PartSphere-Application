package com.example.partsphere.presentation.registration.registration_screen



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.partsphere.R
import com.example.partsphere.ui.theme.Black

@Composable
fun SuccessScreen(
    onBackToLoginClick: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.tick),
                contentDescription = "Success",
                tint = Black,
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 24.dp)
            )

            // Thank you text
            Text(
                text = "Thank you for registering with us!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Black,
                modifier = Modifier.padding(bottom = 32.dp),
                lineHeight = 28.sp
            )

            // Back to Login button
            Button(
                onClick = onBackToLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Black,
                    contentColor = Color.White
                )
            ) {
                Text("Back to Login", fontSize = 16.sp)
            }
        }
    }
}
