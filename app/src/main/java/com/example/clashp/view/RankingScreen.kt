package com.example.clashp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clashp.model.PlayerRanking
import com.example.clashp.ui.theme.ClashPTheme
import com.example.clashp.viewmodel.RankingViewModel

@Composable
fun RankingScreen(
    onBackClick: () -> Unit = {},
    viewModel: RankingViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A0B2E),
            Color(0xFF0D1B2A),
            Color(0xFF0A0A0A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp)
        ) {
            // Título
            Text(
                text = "🏆 Ranking Global 🏆",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF9D4EDD),
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    // Lista de ranking
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1A1A1A)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            // Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFF9D4EDD),
                                                Color(0xFFFF00FF)
                                            )
                                        )
                                    )
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(0.5f)
                                )
                                Text(
                                    text = "Nombre",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = "Puntos",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }

                            // Lista
                            if (uiState.players.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No hay puntuaciones aún.\n¡Sé el primero!",
                                        fontSize = 18.sp,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                LazyColumn {
                                    itemsIndexed(uiState.players) { index, player ->
                                        RankingItem(
                                            position = index + 1,
                                            player = player
                                        )
                                        if (index < uiState.players.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(Color.Gray.copy(alpha = 0.3f))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón volver
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(30.dp),
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
                                        colors = listOf(
                                            Color(0xFF9D4EDD),
                                            Color(0xFFFF00FF)
                                        )
                                    ),
                                    shape = RoundedCornerShape(30.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Volver al Menú",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingItem(
    position: Int,
    player: PlayerRanking
) {
    val positionColor = when (position) {
        1 -> Color(0xFFFFD700) // Gold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> Color.White
    }

    val emoji = when (position) {
        1 -> "🥇"
        2 -> "🥈"
        3 -> "🥉"
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$emoji$position",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = positionColor,
            modifier = Modifier.weight(0.5f)
        )
        Text(
            text = player.name,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = "${player.score}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF00FF),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RankingScreenPreview() {
    ClashPTheme {
        RankingScreen()
    }
}