package com.clashpoints.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.clashpoints.app.data.RankingEntry
import com.clashpoints.app.ui.theme.ClashPointsPink
import com.clashpoints.app.ui.theme.ClashPointsPurple
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun RankingScreen(
    onBackToMenu: () -> Unit
) {
    var rankingList by remember { mutableStateOf<List<RankingEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance()
        val rankingRef = database.getReference("ranking")

        rankingRef.orderByChild("points")
            .limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<RankingEntry>()

                    snapshot.children.forEach { childSnapshot ->
                        val entry = childSnapshot.getValue(RankingEntry::class.java)
                        entry?.let {
                            entries.add(it.copy(key = childSnapshot.key ?: ""))
                        }
                    }

                    rankingList = entries.sortedByDescending { it.points }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            })
    }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
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

            if (isLoading) {
                CircularProgressIndicator(
                    color = ClashPointsPurple,
                    modifier = Modifier.size(50.dp)
                )
            } else {
                // Lista de ranking
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
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
                                        colors = listOf(ClashPointsPurple, ClashPointsPink)
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
                        if (rankingList.isEmpty()) {
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
                                itemsIndexed(rankingList) { index, entry ->
                                    RankingItem(
                                        position = index + 1,
                                        entry = entry
                                    )
                                    if (index < rankingList.size - 1) {
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
                    onClick = onBackToMenu,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(30.dp),
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

@Composable
fun RankingItem(
    position: Int,
    entry: RankingEntry
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
            text = entry.name,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.weight(1.5f)
        )
        Text(
            text = "${entry.points}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = ClashPointsPink,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}