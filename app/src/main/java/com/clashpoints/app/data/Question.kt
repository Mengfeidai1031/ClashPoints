package com.clashpoints.app.data

data class Question(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)

data class QuestionsData(
    val questions: List<Question>
)
