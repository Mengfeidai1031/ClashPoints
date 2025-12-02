package com.example.clashp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clashp.ui.theme.ClashPTheme

data class PlayerRanking(
    val name: String,
    val score: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onBackClick: () -> Unit = {}
){
    // Datos de prueba
    val players = listOf(
        PlayerRanking("Meng Fei", 9620),
        PlayerRanking("Diddy", 7120),
        PlayerRanking("Alex", 6890),
        PlayerRanking("Sarah", 6543),
        PlayerRanking("Mike", 5987),
        PlayerRanking("Luna", 5654),
        PlayerRanking("Pedro", 5432),
        PlayerRanking("Ana", 5210),
        PlayerRanking("Carlos", 4987),
        PlayerRanking("Eva", 4765)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Ranking Global",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Trofeo",
                        tint = Color.Yellow,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1A1A1A))
            )
        },
        containerColor = Color.Black
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Top 10 Jugadores",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(players) { index, player ->
                    RankingItem(
                        position = index + 1,
                        player = player
                    )
                }
            }
        }
    }
}

@Composable
fun RankingItem(position: Int, player: PlayerRanking) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun RankingScreenPreview() {
    ClashPTheme {
        RankingScreen()
    }
}