package com.example.clashp.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clashp.model.Category
import com.example.clashp.model.GameUiState
import com.example.clashp.model.Question
import com.example.clashp.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class GameViewModel(
    private val questionRepository: QuestionRepository = QuestionRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timer: CountDownTimer? = null
    private val availableQuestions = mutableListOf<Question>()

    fun startGame(playerName: String) {
        _uiState.value = GameUiState(
            playerName = playerName,
            showRoulette = true
        )
    }

    fun spinRoulette(selectedCategory: Category) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            selectedCategory = selectedCategory
        )

        // Cargar preguntas desde Firebase
        viewModelScope.launch {
            val result = questionRepository.getQuestionsByCategory(
                categoryName = selectedCategory.displayName,
                limit = 4
            )

            result.fold(
                onSuccess = { questions ->
                    availableQuestions.clear()
                    availableQuestions.addAll(questions)

                    _uiState.value = _uiState.value.copy(
                        showRoulette = false,
                        isLoading = false
                    )

                    loadNextQuestion()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar preguntas: ${error.message}"
                    )
                }
            )
        }
    }

    private fun loadNextQuestion() {
        val currentState = _uiState.value

        // Verificar si el juego terminó (12 preguntas)
        if (currentState.currentQuestionIndex >= 12) {
            endGame()
            return
        }

        // Verificar si necesitamos mostrar la ruleta (cada 4 preguntas)
        if (currentState.questionsInCurrentRound >= 4) {
            _uiState.value = currentState.copy(
                showRoulette = true,
                questionsInCurrentRound = 0,
                currentRound = currentState.currentRound + 1,
                hasAnswered = false,
                lastAnswerWasCorrect = null,
                pointsEarnedLastQuestion = 0
            )
            stopTimer()
            return
        }

        // Cargar siguiente pregunta
        val questionIndexInRound = currentState.questionsInCurrentRound
        if (questionIndexInRound < availableQuestions.size) {
            _uiState.value = currentState.copy(
                currentQuestion = availableQuestions[questionIndexInRound],
                timeRemaining = 25,
                hasAnswered = false,
                lastAnswerWasCorrect = null,
                pointsEarnedLastQuestion = 0
            )
            startTimer()
        }
    }

    private fun startTimer() {
        stopTimer() // Para asegurarmee

        timer = object : CountDownTimer(25000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt() + 1
                _uiState.value = _uiState.value.copy(timeRemaining = secondsRemaining)
            }

            override fun onFinish() {
                // Tiempo agotado = 0 puntos
                handleTimeOut()
            }
        }.start()
    }

    private fun stopTimer() {
        timer?.cancel()
        timer = null
    }

    private fun handleTimeOut() {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            timeRemaining = 0,
            hasAnswered = true,
            lastAnswerWasCorrect = false,
            pointsEarnedLastQuestion = 0
        )

        stopTimer()

        // Esperar un momento antes de cargar siguiente pregunta
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // 2 segundos para ver resultado
            moveToNextQuestion()
        }
    }

    fun checkAnswer(selectedOption: String) {
        val currentState = _uiState.value

        // Evitar responder dos veces
        if (currentState.hasAnswered) return

        val currentQuestion = currentState.currentQuestion ?: return
        val isCorrect = selectedOption == currentQuestion.correctAnswer

        stopTimer()

        // Calcular puntos basado en tiempo
        val pointsEarned = if (isCorrect) {
            calculatePoints(currentState.timeRemaining)
        } else {
            0
        }

        _uiState.value = currentState.copy(
            hasAnswered = true,
            lastAnswerWasCorrect = isCorrect,
            pointsEarnedLastQuestion = pointsEarned,
            totalScore = currentState.totalScore + pointsEarned
        )

        // Esperar antes de cargar siguiente pregunta
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // 2 segundos para ver resultado
            moveToNextQuestion()
        }
    }

    private fun calculatePoints(timeRemaining: Int): Int {
        // formula: puntos = 1000 * (1 - (TiempoTranscurrido / 25))
        val timeElapsed = 25 - timeRemaining
        val points = 1000 * (1 - (timeElapsed.toFloat() / 25))
        return max(0, points.toInt())
    }

    private fun moveToNextQuestion() {
        val currentState = _uiState.value

        _uiState.value = currentState.copy(
            currentQuestionIndex = currentState.currentQuestionIndex + 1,
            questionsInCurrentRound = currentState.questionsInCurrentRound + 1
        )

        loadNextQuestion()
    }

    private fun endGame() {
        stopTimer()
        _uiState.value = _uiState.value.copy(
            isGameOver = true,
            showRoulette = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}