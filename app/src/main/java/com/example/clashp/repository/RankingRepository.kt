package com.example.clashp.repository

import com.example.clashp.model.PlayerRanking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RankingRepository {
    fun getRankings(): Flow<List<PlayerRanking>> = flow {
        // Datos de prueba - en el futuro conectarás con API/BD
        emit(
            listOf(
                PlayerRanking(id = "1", name = "Meng Fei", score = 9620),
                PlayerRanking(id = "2", name = "Diddy", score = 7120),
                PlayerRanking(id = "3", name = "Alex", score = 6890),
                PlayerRanking(id = "4", name = "Sarah", score = 6543),
                PlayerRanking(id = "5", name = "Mike", score = 5987),
                PlayerRanking(id = "6", name = "Luna", score = 5654),
                PlayerRanking(id = "7", name = "Pedro", score = 5432),
                PlayerRanking(id = "8", name = "Ana", score = 5210),
                PlayerRanking(id = "9", name = "Carlos", score = 4987),
                PlayerRanking(id = "10", name = "Eva", score = 4765)
            )
        )
    }
}
