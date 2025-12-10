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



    // TEMPORAL ---  BORRAR DESPUÉS

    // SOLO PARA TESTING - Ejecutar una vez para poblar la base de datos
    fun uploadMockQuestions() {
        val mockQuestions = listOf(
            // Historia
            Question("", "¿En qué año cayó el Imperio Romano de Occidente?",
                listOf("476 d.C.", "395 d.C.", "1453 d.C.", "410 d.C."), "476 d.C.", "Historia"),
            Question("", "¿Quién fue el primer presidente de Estados Unidos?",
                listOf("George Washington", "Thomas Jefferson", "Abraham Lincoln", "Benjamin Franklin"), "George Washington", "Historia"),
            Question("", "¿En qué año comenzó la Segunda Guerra Mundial?",
                listOf("1939", "1914", "1941", "1945"), "1939", "Historia"),
            Question("", "¿Qué faraón egipcio mandó construir la Gran Pirámide de Giza?",
                listOf("Keops", "Tutankamón", "Ramsés II", "Cleopatra"), "Keops", "Historia"),

            // Deportes
            Question("", "¿Cuántos jugadores hay en un equipo de fútbol?",
                listOf("11", "10", "12", "9"), "11", "Deportes"),
            Question("", "¿En qué deporte se utiliza un 'birdie'?",
                listOf("Bádminton", "Golf", "Tenis", "Cricket"), "Bádminton", "Deportes"),
            Question("", "¿Cuántos anillos olímpicos hay?",
                listOf("5", "4", "6", "7"), "5", "Deportes"),
            Question("", "¿En qué país se celebraron los primeros Juegos Olímpicos modernos?",
                listOf("Grecia", "Francia", "Inglaterra", "Italia"), "Grecia", "Deportes"),

            // Arte
            Question("", "¿Quién pintó la Mona Lisa?",
                listOf("Leonardo da Vinci", "Miguel Ángel", "Rafael", "Donatello"), "Leonardo da Vinci", "Arte"),
            Question("", "¿Qué pintor cortó parte de su oreja?",
                listOf("Vincent van Gogh", "Pablo Picasso", "Salvador Dalí", "Claude Monet"), "Vincent van Gogh", "Arte"),
            Question("", "¿Quién pintó 'La noche estrellada'?",
                listOf("Vincent van Gogh", "Pablo Picasso", "Claude Monet", "Rembrandt"), "Vincent van Gogh", "Arte"),
            Question("", "¿Qué artista español pintó 'El Guernica'?",
                listOf("Pablo Picasso", "Salvador Dalí", "Joan Miró", "Francisco Goya"), "Pablo Picasso", "Arte"),

            // Geografía
            Question("", "¿Cuál es la capital de Australia?",
                listOf("Canberra", "Sídney", "Melbourne", "Brisbane"), "Canberra", "Geografía"),
            Question("", "¿Qué río es el más largo del mundo?",
                listOf("Amazonas", "Nilo", "Yangtsé", "Misisipi"), "Amazonas", "Geografía"),
            Question("", "¿Cuál es el país más grande del mundo por superficie?",
                listOf("Rusia", "Canadá", "China", "Estados Unidos"), "Rusia", "Geografía"),
            Question("", "¿En qué continente se encuentra el desierto del Sahara?",
                listOf("África", "Asia", "América", "Oceanía"), "África", "Geografía"),

            // Entretenimiento
            Question("", "¿Quién interpretó a Iron Man en las películas de Marvel?",
                listOf("Robert Downey Jr.", "Chris Evans", "Chris Hemsworth", "Mark Ruffalo"), "Robert Downey Jr.", "Entretenimiento"),
            Question("", "¿En qué año se estrenó la primera película de Harry Potter?",
                listOf("2001", "1999", "2000", "2002"), "2001", "Entretenimiento"),
            Question("", "¿Qué serie de televisión tiene lugar en Westeros?",
                listOf("Game of Thrones", "The Witcher", "Vikings", "The Last Kingdom"), "Game of Thrones", "Entretenimiento"),
            Question("", "¿Quién dirigió la película 'Titanic'?",
                listOf("James Cameron", "Steven Spielberg", "Christopher Nolan", "Martin Scorsese"), "James Cameron", "Entretenimiento"),

            // Ciencia
            Question("", "¿Cuál es el símbolo químico del oro?",
                listOf("Au", "Ag", "Fe", "Cu"), "Au", "Ciencia"),
            Question("", "¿Cuántos planetas hay en el sistema solar?",
                listOf("8", "7", "9", "10"), "8", "Ciencia"),
            Question("", "¿Cuál es el órgano más grande del cuerpo humano?",
                listOf("La piel", "El hígado", "El cerebro", "Los pulmones"), "La piel", "Ciencia"),
            Question("", "¿A qué velocidad viaja la luz en el vacío?",
                listOf("300,000 km/s", "150,000 km/s", "400,000 km/s", "200,000 km/s"), "300,000 km/s", "Ciencia")
        )

        viewModelScope.launch {
            mockQuestions.forEach { question ->
                questionRepository.addQuestion(question)
            }
            android.util.Log.d("GameViewModel", "Preguntas subidas a Firebase")
        }
    }


}