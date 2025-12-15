package com.clashpoints.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clashpoints.app.R
import com.clashpoints.app.ui.theme.ClashPointsCyan
import com.clashpoints.app.ui.theme.ClashPointsPink
import com.clashpoints.app.ui.theme.ClashPointsPurple

@Composable
fun MenuScreen(
    onStartClick: () -> Unit,
    onRankingClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),      // Negro
                        Color(0xFF1A0B2E),      // Morado oscuro
                        Color(0xFF0D1B2A),      // Azul oscuro
                        Color(0xFF0A0A0A)       // Negro
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo más grande
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "ClashPoints Logo",
                modifier = Modifier
                    .size(320.dp)
                    .padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Start Button
            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .width(280.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ClashPointsPurple, ClashPointsPink)
                            ),
                            shape = RoundedCornerShape(35.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Ranking Button
            Button(
                onClick = onRankingClick,
                modifier = Modifier
                    .width(280.dp)
                    .height(70.dp),
                shape = RoundedCornerShape(35.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ClashPointsPurple, ClashPointsPink)
                            ),
                            shape = RoundedCornerShape(35.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ranking",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Settings Button (más pequeño)
            Button(
                onClick = { /* TODO: Add settings */ },
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2A2A2A)
                )
            ) {
                Text(
                    text = "Ajustes",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}