package com.clashpoints.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.clashpoints.app.data.Question
import com.clashpoints.app.repository.FirebaseRepository
import com.clashpoints.app.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val questionRepository: QuestionRepository
    private val firebaseRepository: FirebaseRepository

    init {
        try {
            questionRepository = QuestionRepository(application.applicationContext)
            firebaseRepository = FirebaseRepository()
            Log.d("GameViewModel", "ViewModel initialized successfully")
        } catch (e: Exception) {
            Log.e("GameViewModel", "Error initializing ViewModel", e)
            throw e
        }
    }

    // Estado del jugador
    private val _playerName = MutableStateFlow("")
    val playerName: StateFlow<String> = _playerName.asStateFlow()

    // Estado del juego
    private val _currentRound = MutableStateFlow(1)
    val currentRound: StateFlow<Int> = _currentRound.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore.asStateFlow()

    private val _currentCategory = MutableStateFlow("")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val _currentQuestions = MutableStateFlow<List<Question>>(emptyList())
    val currentQuestions: StateFlow<List<Question>> = _currentQuestions.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    // Historial de respuestas
    private val answeredQuestions = mutableListOf<Pair<Question, Boolean>>()

    fun setPlayerName(name: String) {
        _playerName.value = name
    }

    fun selectCategory(category: String) {
        _currentCategory.value = category

        // Cargar 4 preguntas de la categoría
        val questions = questionRepository.getQuestionsByCategory(category, 4)
        _currentQuestions.value = questions
        _currentQuestionIndex.value = 0

        // Establecer la primera pregunta
        if (questions.isNotEmpty()) {
            _currentQuestion.value = questions[0]
        }
    }

    fun answerQuestion(selectedAnswer: Int, pointsEarned: Int) {
        val question = _currentQuestion.value ?: return
        val isCorrect = selectedAnswer == question.correctAnswer

        // Registrar respuesta
        answeredQuestions.add(Pair(question, isCorrect))

        // Actualizar puntuación
        _totalScore.value += pointsEarned
    }

    fun calculatePoints(isCorrect: Boolean, timeRemaining: Int): Int {
        if (!isCorrect) {
            // Respuesta incorrecta: restar 50 puntos
            return -50
        }

        // Respuesta correcta: calcular puntos basados en tiempo (tipo Kahoot)
        // Fórmula: 500 puntos base + hasta 1000 puntos bonus por velocidad
        // Si quedan 25 segundos (máximo): 500 + 1000 = 1500 puntos
        // Si quedan 0 segundos: 500 + 0 = 500 puntos

        val basePoints = 500
        val timeBonus = (timeRemaining.toFloat() / 25f * 1000f).toInt()

        return basePoints + timeBonus
    }

    fun moveToNextQuestion(): String {
        val currentIndex = _currentQuestionIndex.value
        val questions = _currentQuestions.value

        return if (currentIndex < questions.size - 1) {
            // Siguiente pregunta en la misma ronda
            _currentQuestionIndex.value = currentIndex + 1
            _currentQuestion.value = questions[currentIndex + 1]
            "next_question"
        } else if (_currentRound.value < 3) {
            // Siguiente ronda
            _currentRound.value += 1
            "next_round"
        } else {
            // Juego completado
            "game_complete"
        }
    }

    fun saveScoreToFirebase() {
        viewModelScope.launch {
            firebaseRepository.saveScore(
                name = _playerName.value,
                points = _totalScore.value
            ) { success ->
                // Callback de éxito/error
                if (success) {
                    Log.d("GameViewModel", "Puntuación guardada correctamente")
                } else {
                    Log.e("GameViewModel", "Error al guardar puntuación")
                }
            }
        }
    }

    fun resetGame() {
        Log.d("GameViewModel", "Resetting game")
        _currentRound.value = 1
        _currentQuestionIndex.value = 0
        _totalScore.value = 0
        _currentCategory.value = ""
        _currentQuestions.value = emptyList()
        _currentQuestion.value = null
        answeredQuestions.clear()
    }

    // Funciones auxiliares
    fun getCorrectAnswersCount(): Int {
        return answeredQuestions.count { it.second }
    }

    fun getIncorrectAnswersCount(): Int {
        return answeredQuestions.count { !it.second }
    }

    fun getTotalQuestionsAnswered(): Int {
        return answeredQuestions.size
    }
}