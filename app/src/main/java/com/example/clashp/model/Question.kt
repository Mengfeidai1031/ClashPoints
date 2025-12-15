package com.example.clashp.model

import com.google.firebase.firestore.DocumentId

data class Question(
    @DocumentId
    val id: String = "",
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val category: String = ""
) {
    fun getCategoryEnum(): Category {
        return try {
            Category.entries.find { it.displayName.equals(category, ignoreCase = true) }
                ?: Category.HISTORIA
        } catch (_: Exception) {
            Category.HISTORIA
        }
    }
}
