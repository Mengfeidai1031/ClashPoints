package com.example.clashp.repository

import com.example.clashp.model.PlayerRanking
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RankingRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val scoresCollection = firestore.collection("scores")
    // obtener los rankings
    fun getRankings(limit: Int = 10): Flow<Result<List<PlayerRanking>>> = callbackFlow {
        val listener = scoresCollection
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                val rankings = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        PlayerRanking(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            score = doc.getLong("score")?.toInt() ?: 0,
                            timestamp = doc.getTimestamp("timestamp")
                        )
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(Result.success(rankings))
            }
        awaitClose { listener.remove() }
    }
    suspend fun addScore(name: String, score: Int): Result<String> {
        return try {
            val data = hashMapOf(
                "name" to name,
                "score" to score,
                "timestamp" to Timestamp.now()
            )

            val docRef = scoresCollection.add(data).await()
            Result.success(docRef.id)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun updateScore(playerId: String, newScore: Int): Result<Unit>{
        return try {
            scoresCollection.document(playerId)
                .update("score", newScore, "timestamp", Timestamp.now())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
