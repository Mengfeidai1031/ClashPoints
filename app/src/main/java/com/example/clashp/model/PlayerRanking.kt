package com.example.clashp.model

import com.google.firebase.Timestamp

data class PlayerRanking(
    val id: String = "",
    val name: String = "",
    val score: Int = 0,
    val timestamp: Timestamp? = null
)