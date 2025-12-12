package com.example.clashp.model

data class GameUiState(
    val playerName: String = "",
    val currentQuestion: Question? = null,
    val currentQuestionIndex: Int = 0, // 0-11 (12 preguntas total)
    val questionsInCurrentRound: Int = 0, // 0-3 (4 preguntas por ronda)
    val currentRound: Int = 1, // 1, 2, 3
    val totalScore: Int = 0,
    val timeRemaining: Int = 25, // segundos
    val selectedCategory: Category? = null,
    val showRoulette: Boolean = true, // Empieza mostrando ruleta
    val isLoading: Boolean = false,
    val isGameOver: Boolean = false,
    val hasAnswered: Boolean = false,
    val selectedAnswer: String? = null,
    val lastAnswerWasCorrect: Boolean? = null,
    val pointsEarnedLastQuestion: Int = 0,
    val errorMessage: String? = null
) {
    val totalQuestionsAnswered: Int
        get() = (currentRound - 1) * 4 + questionsInCurrentRound

    val shouldShowRoulette: Boolean
        get() = questionsInCurrentRound == 0 && !isGameOver
}
