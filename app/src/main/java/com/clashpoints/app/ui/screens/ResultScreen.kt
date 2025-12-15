package com.clashpoints.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clashpoints.app.ui.theme.ClashPointsPink
import com.clashpoints.app.ui.theme.ClashPointsPurple
import com.clashpoints.app.viewmodel.GameViewModel

@Composable
fun ResultScreen(
    gameViewModel: GameViewModel,
    onSaveScore: () -> Unit,
    onBackToMenu: () -> Unit
) {
    val totalScore = gameViewModel.totalScore.value
    val playerName = gameViewModel.playerName.value
    var showSaveDialog by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        Color(0xFF1A0B2E),
                        Color(0xFF0D1B2A),
                        Color(0xFF0A0A0A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "¡Juego Terminado!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Card de puntuación - centrada
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp)
                ) {
                    Text(
                        text = playerName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClashPointsPurple
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Puntuación Final",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "$totalScore",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClashPointsPink
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "puntos",
                        fontSize = 20.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (showSaveDialog) {
                Text(
                    text = "¿Guardar puntuación en el ranking global?",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Botones del mismo tamaño exacto
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón Sí
                    Button(
                        onClick = {
                            gameViewModel.saveScoreToFirebase()
                            showSaveDialog = false
                            onSaveScore()
                        },
                        modifier = Modifier
                            .width(280.dp)
                            .height(70.dp),
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
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
                                text = "Sí",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Botón No
                    Button(
                        onClick = {
                            showSaveDialog = false
                            onBackToMenu()
                        },
                        modifier = Modifier
                            .width(280.dp)
                            .height(70.dp),
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color(0xFF2A2A2A),
                                    shape = RoundedCornerShape(35.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            } else {
                Button(
                    onClick = onBackToMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(35.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A2A2A)
                    )
                ) {
                    Text(
                        text = "Volver al Menú",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}