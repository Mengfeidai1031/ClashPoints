package com.example.clashp.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.clashp.model.Category
import com.example.clashp.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    playerName: String,
    viewModel: GameViewModel,
    onGameFinished: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Iniciar juego si no está iniciado
    LaunchedEffect(Unit) {
        if (uiState.playerName.isEmpty()) {
            viewModel.startGame(playerName)
            // esto es solo para subir las preguntas una vez, luego lo quitamos
            // viewModel.uploadMockQuestions()
        }
    }

    // Detectar fin del juego
    LaunchedEffect(uiState.isGameOver) {
        if (uiState.isGameOver) {
            onGameFinished(uiState.totalScore)
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A0520),
            Color(0xFF0D1B2A)
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Puntos: ${uiState.totalScore}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Salir",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
                .padding(paddingValues)
        ) {
            AnimatedContent(
                targetState = uiState.showRoulette,
                label = "game_content"
            ) { showRoulette ->
                if (showRoulette) {
                    RouletteView(
                        onCategorySelected = { category ->
                            viewModel.spinRoulette(category)
                        }
                    )
                } else {
                    QuestionView(
                        uiState = uiState,
                        onAnswerSelected = { answer ->
                            viewModel.checkAnswer(answer)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RouletteView(
    onCategorySelected: (Category) -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val scope = rememberCoroutineScope()

    val categories = remember {
        listOf(
            Category.HISTORIA,
            Category.DEPORTES,
            Category.ARTE,
            Category.GEOGRAFIA,
            Category.ENTRETENIMIENTO,
            Category.CIENCIA
        )
    }

    val categoryColors = mapOf(
        Category.HISTORIA to Color(0xFF9D4EDD),
        Category.DEPORTES to Color(0xFF4CAF50),
        Category.ARTE to Color(0xFFE91E8C),
        Category.GEOGRAFIA to Color(0xFF00D9FF),
        Category.ENTRETENIMIENTO to Color(0xFFFF6B35),
        Category.CIENCIA to Color(0xFFFFC107)
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A0520),
            Color(0xFF0D1B2A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Gira la ruleta",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Flecha indicadora
            Canvas(modifier = Modifier.size(40.dp, 30.dp)) {
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width / 2, size.height)
                    lineTo(0f, 0f)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path = path, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ruleta
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    categories.forEachIndexed { index, category ->
                        val startAngle = index * 60f - 90f
                        drawArc(
                            color = categoryColors[category] ?: Color.Gray,
                            startAngle = startAngle,
                            sweepAngle = 60f,
                            useCenter = true,
                            topLeft = Offset(centerX - radius, centerY - radius),
                            size = Size(radius * 2, radius * 2)
                        )
                    }

                    drawCircle(
                        color = Color(0xFF2A2A2A),
                        radius = 50f,
                        center = Offset(centerX, centerY)
                    )
                }

                // Nombres
                categories.forEachIndexed { index, category ->
                    val angle = Math.toRadians((index * 60 - 60).toDouble())
                    val textRadius = 105.0
                    val x = (kotlin.math.cos(angle) * textRadius).toFloat().dp
                    val y = (kotlin.math.sin(angle) * textRadius).toFloat().dp

                    Box(
                        modifier = Modifier
                            .offset(x, y)
                            .rotate(index * 60f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category.displayName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Resultado
            if (selectedCategory != null) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = categoryColors[selectedCategory] ?: Color.Gray
                ) {
                    Text(
                        text = "¡${selectedCategory!!.displayName}!",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Botón
            Button(
                onClick = {
                    if (!isSpinning) {
                        isSpinning = true
                        selectedCategory = null

                        scope.launch {
                            // Seleccionar categoría
                            val selectedIndex = (0..5).random()
                            val selectedCat = categories[selectedIndex]

                            // Importante: Para centrar, rotar a -index*60 - 30
                            val targetAngle = -(selectedIndex * 60f + 30f)
                            val targetRotation = 360f * 5 + targetAngle

                            val animationDuration = 3000
                            val startTime = System.currentTimeMillis()

                            // Animar
                            while (System.currentTimeMillis() - startTime < animationDuration) {
                                val elapsed = (System.currentTimeMillis() - startTime).toFloat()
                                val progress = elapsed / animationDuration
                                val easedProgress = 1f - (1f - progress) * (1f - progress) * (1f - progress)
                                rotation = targetRotation * easedProgress
                                kotlinx.coroutines.delay(16)
                            }

                            rotation = targetRotation

                            // Mostrar resultado
                            selectedCategory = selectedCat
                            kotlinx.coroutines.delay(2000) // Esperar 2 segundos

                            // Continuar al juego
                            isSpinning = false
                            onCategorySelected(selectedCat)
                        }
                    }
                },
                enabled = !isSpinning,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF00FF),
                    disabledContainerColor = Color(0xFF666666)
                )
            ) {
                Text(
                    text = if (isSpinning) "Girando..." else "Girar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}






@Composable
private fun QuestionView(
    uiState: com.example.clashp.model.GameUiState,
    onAnswerSelected: (String) -> Unit
) {
    val question = uiState.currentQuestion ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Pregunta y Puntos
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pregunta ${uiState.currentQuestionIndex + 1}/12",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer circular con Canvas
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val progress = uiState.timeRemaining / 25f
                    val sweepAngle = 360f * progress

                    // Círculo de fondo
                    drawCircle(
                        color = Color(0xFF333333),
                        radius = size.minDimension / 2
                    )

                    // progreso
                    val timerColor = when {
                        uiState.timeRemaining > 15 -> Color(0xFF00FF00) // Verde
                        uiState.timeRemaining > 8 -> Color(0xFFFFAA00)  // Amarillo/Naranja
                        else -> Color(0xFFFF0000) // Rojo
                    }

                    drawArc(
                        color = timerColor,
                        startAngle = -90f,
                        sweepAngle = -sweepAngle,
                        useCenter = true
                    )

                    // Círculo interior negro
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2.8f
                    )
                }

                // Número del tiempo
                Text(
                    text = "${uiState.timeRemaining}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Categoría
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFF00FF)
            ) {
                Text(
                    text = question.getCategoryEnum().displayName,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pregunta en un Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = question.text,
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Opciones como Cards clickeables
            question.options.forEach { option ->
                val isCorrect = option == question.correctAnswer
                val isSelected = uiState.hasAnswered && isCorrect
                val isWrong = uiState.hasAnswered && !isCorrect

                val backgroundColor = when {
                    isSelected -> Color(0xFF00FF00).copy(alpha = 0.3f)
                    isWrong && !isSelected -> Color(0xFF2A2A2A)
                    else -> Color(0xFF2A2A2A)
                }

                val borderColor = when {
                    isSelected -> Color(0xFF00FF00)
                    isWrong -> Color(0xFF2A2A2A)
                    else -> Color(0xFF444444)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .border(2.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable(enabled = !uiState.hasAnswered) {
                            onAnswerSelected(option)
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

            // Feedback de puntos ganados
            if (uiState.hasAnswered) {
                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.lastAnswerWasCorrect == true) {
                    Text(
                        text = "¡Correcto! +${uiState.pointsEarnedLastQuestion} pts",
                        color = Color(0xFF00FF00),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Incorrecto - 0 pts",
                        color = Color(0xFFFF0000),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

