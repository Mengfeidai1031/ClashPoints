package com.clashpoints.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clashpoints.app.ui.theme.ClashPointsPink
import com.clashpoints.app.ui.theme.ClashPointsPurple
import com.clashpoints.app.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun QuestionScreen(
    gameViewModel: GameViewModel,
    onRoundComplete: () -> Unit,
    onGameComplete: () -> Unit
) {
    val currentQuestion by gameViewModel.currentQuestion.collectAsState()
    var timeRemaining by remember { mutableStateOf(25) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }

    // Temporizador
    LaunchedEffect(currentQuestion) {
        timeRemaining = 25
        selectedAnswer = null
        showResult = false

        while (timeRemaining > 0 && selectedAnswer == null) {
            delay(1000)
            timeRemaining--
        }

        if (selectedAnswer == null) {
            // Tiempo agotado
            gameViewModel.answerQuestion(-1, 0)
            showResult = true
            isCorrect = false
            delay(2000)
            moveToNextQuestion(gameViewModel, onRoundComplete, onGameComplete)
        }
    }

    currentQuestion?.let { question ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0A0A),
                            Color(0xFF1A0B2E),
                            Color(0xFF0D1B2A),
                            Color(0xFF0A0A0A)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pregunta ${gameViewModel.currentQuestionIndex.value + 1}/4",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Puntos: ${gameViewModel.totalScore.value}",
                        fontSize = 18.sp,
                        color = ClashPointsPink,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Timer circular
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val progress = timeRemaining / 25f
                        // sweepAngle representa cuánto queda de tiempo
                        val sweepAngle = 360f * progress

                        // Background circle
                        drawCircle(
                            color = Color.DarkGray,
                            radius = size.minDimension / 2
                        )

                        // Progress arc - se consume en sentido HORARIO
                        // Para que se consuma de derecha a izquierda (como agujas del reloj):
                        // Empieza en arriba (-90°) y el arco crece en sentido HORARIO
                        // Cuando timeRemaining baja, sweepAngle baja, el arco se hace más pequeño
                        // y desaparece en sentido horario (derecha → abajo → izquierda)
                        drawArc(
                            color = when {
                                timeRemaining > 15 -> Color.Green
                                timeRemaining > 8 -> Color.Yellow
                                else -> Color.Red
                            },
                            startAngle = -90f + (360f - sweepAngle),  // Se mueve en horario
                            sweepAngle = sweepAngle,  // Lo que queda
                            useCenter = true
                        )

                        // Inner circle
                        drawCircle(
                            color = Color.Black,
                            radius = size.minDimension / 2.5f
                        )
                    }

                    Text(
                        text = "$timeRemaining",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Categoría
                Text(
                    text = question.category,
                    fontSize = 16.sp,
                    color = ClashPointsPurple,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Pregunta
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = question.question,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Opciones
                question.options.forEachIndexed { index, option ->
                    val backgroundColor = when {
                        showResult && index == question.correctAnswer -> Color.Green.copy(alpha = 0.3f)
                        showResult && index == selectedAnswer && index != question.correctAnswer -> Color.Red.copy(alpha = 0.3f)
                        selectedAnswer == index -> ClashPointsPurple.copy(alpha = 0.5f)
                        else -> Color(0xFF2A2A2A)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp, horizontal = 8.dp)
                            .clickable(enabled = !showResult && selectedAnswer == null) {
                                selectedAnswer = index
                                val pointsEarned = gameViewModel.calculatePoints(
                                    isCorrect = index == question.correctAnswer,
                                    timeRemaining = timeRemaining
                                )
                                gameViewModel.answerQuestion(index, pointsEarned)
                                showResult = true
                                isCorrect = index == question.correctAnswer
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = backgroundColor
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = option,
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                if (showResult) {
                    Spacer(modifier = Modifier.height(16.dp))

                    LaunchedEffect(Unit) {
                        delay(2000)
                        moveToNextQuestion(gameViewModel, onRoundComplete, onGameComplete)
                    }

                    Button(
                        onClick = {
                            moveToNextQuestion(gameViewModel, onRoundComplete, onGameComplete)
                        },
                        modifier = Modifier
                            .width(200.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClashPointsPurple
                        )
                    ) {
                        Text("Siguiente", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

private fun moveToNextQuestion(
    gameViewModel: GameViewModel,
    onRoundComplete: () -> Unit,
    onGameComplete: () -> Unit
) {
    val result = gameViewModel.moveToNextQuestion()
    when (result) {
        "next_question" -> {
            // La UI se actualizará automáticamente
        }
        "next_round" -> {
            onRoundComplete()
        }
        "game_complete" -> {
            onGameComplete()
        }
    }
}