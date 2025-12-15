package com.clashpoints.app.ui.screens

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.clashpoints.app.ui.theme.ClashPointsPink
import com.clashpoints.app.ui.theme.ClashPointsPurple
import com.clashpoints.app.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RouletteScreen(
    gameViewModel: GameViewModel,
    onCategorySelected: () -> Unit
) {
    var isSpinning by remember { mutableStateOf(false) }
    var rotation by remember { mutableStateOf(0f) }

    // Categorías fijas con sus colores
    val categoryColors = listOf(
        Pair("Geografia", Color(0xFF9D4EDD)),      // Morado
        Pair("Ciencia", Color(0xFFE91E8C)),        // Rosa
        Pair("Historia", Color(0xFF00D9FF)),       // Cian
        Pair("Deportes", Color(0xFFFF6B35)),       // Naranja
        Pair("Arte", Color(0xFF4CAF50)),           // Verde
        Pair("Entretenimiento", Color(0xFFFFC107)) // Amarillo
    )

    LaunchedEffect(isSpinning) {
        if (isSpinning) {
            // Seleccionar categoría aleatoria
            val selectedIndex = (0..5).random()

            // Para que selectedIndex quede arriba, necesitamos rotar HACIA ATRÁS
            // Si selectedIndex = 0 (Geografia), rotation = 0° (ya está arriba)
            // Si selectedIndex = 1 (Ciencia), necesitamos rotar -60° (hacia atrás)
            // Si selectedIndex = 2 (Historia), necesitamos rotar -120°
            // Pero como rotamos muchas vueltas, sumamos 360*5 para efecto visual
            val targetRotation = 360f * 5 - (selectedIndex * 60f)

            // Animación de 4 segundos
            val animationDuration = 4000
            val startTime = System.currentTimeMillis()

            while (System.currentTimeMillis() - startTime < animationDuration) {
                val elapsed = (System.currentTimeMillis() - startTime).toFloat()
                val progress = elapsed / animationDuration

                // Desaceleración cubic
                val easedProgress = 1f - (1f - progress) * (1f - progress) * (1f - progress)

                rotation = targetRotation * easedProgress
                delay(16)
            }

            rotation = targetRotation % 360f

            // La categoría seleccionada es simplemente selectedIndex
            // porque ya calculamos la rotación para que quede arriba
            val selectedCategory = categoryColors[selectedIndex].first
            gameViewModel.selectCategory(selectedCategory)

            delay(500)
            isSpinning = false
            onCategorySelected()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0A),
                        Color(0xFF1A0B2E),
                        Color(0xFF0A0A0A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Ronda ${gameViewModel.currentRound.value}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Flecha indicadora
            Canvas(modifier = Modifier.size(40.dp, 30.dp)) {
                val path = Path().apply {
                    moveTo(size.width / 2, size.height)
                    lineTo(0f, 0f)
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(
                    path = path,
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ruleta con nombres verticales centrados en cada segmento
            Box(
                modifier = Modifier.size(340.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    val centerX = size.width / 2
                    val centerY = size.height / 2

                    rotate(rotation, pivot = Offset(centerX, centerY)) {
                        // Dibujar segmentos CENTRADOS en la flecha
                        // Para que el centro del segmento 0 esté en -90° (flecha),
                        // el segmento debe empezar en -90° - 30° = -120°
                        for (i in 0..5) {
                            val startAngle = -120f + (i * 60f)
                            drawArc(
                                color = categoryColors[i].second,
                                startAngle = startAngle,
                                sweepAngle = 60f,
                                useCenter = true,
                                topLeft = Offset(centerX - radius, centerY - radius),
                                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                            )
                        }

                        // Dibujar líneas divisorias desde arriba
                        for (i in 0..5) {
                            val angle = Math.toRadians((-120f + i * 60f).toDouble())
                            val endX = centerX + (radius * cos(angle)).toFloat()
                            val endY = centerY + (radius * sin(angle)).toFloat()
                            drawLine(
                                color = Color.White,
                                start = Offset(centerX, centerY),
                                end = Offset(endX, endY),
                                strokeWidth = 3f
                            )
                        }

                        // Dibujar nombres VERTICALES en el centro de cada "queso"
                        categoryColors.forEachIndexed { index, (category, _) ->
                            // Tamaño de texto basado en longitud de la palabra
                            // Cuanto más corta, más grande puede ser
                            val textSize = when(category.length) {
                                4 -> 38f      // "Arte" - el más grande
                                8 -> 32f      // "Geografia", "Deportes" - grande
                                7 -> 34f      // "Ciencia", "Historia" - medio-grande
                                15 -> 26f     // "Entretenimiento" - el más pequeño (justo)
                                else -> 30f   // Por defecto
                            }

                            val textPaint = Paint().apply {
                                color = android.graphics.Color.WHITE
                                this.textSize = textSize
                                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                                textAlign = Paint.Align.CENTER
                                setShadowLayer(3f, 0f, 0f, android.graphics.Color.BLACK)
                            }

                            // Centro del segmento (ahora empiezan en -120°)
                            // Segmento i va de -120°+i*60° a -120°+(i+1)*60°
                            // Centro: -120° + i*60° + 30°
                            val segmentCenterAngle = Math.toRadians((-120f + index * 60f + 30f).toDouble())

                            // Distancia desde el centro de la ruleta
                            val textRadius = radius * 0.55f

                            // Calcular posición X,Y del centro del texto usando coordenadas polares
                            val textCenterX = centerX + (textRadius * Math.cos(segmentCenterAngle)).toFloat()
                            val textCenterY = centerY + (textRadius * Math.sin(segmentCenterAngle)).toFloat()

                            drawContext.canvas.nativeCanvas.apply {
                                save()

                                // Trasladar al centro donde irá el texto
                                translate(textCenterX, textCenterY)

                                // Rotar para que el texto quede vertical apuntando hacia afuera
                                rotate((-120f + index * 60f + 30f + 90f), 0f, 0f)

                                // Dibujar cada letra verticalmente con espaciado proporcional
                                val letterSpacing = textSize * 0.92f  // Proporcional al tamaño
                                val totalHeight = (category.length - 1) * letterSpacing
                                val startY = -totalHeight / 2

                                category.forEachIndexed { charIndex, char ->
                                    drawText(
                                        char.toString(),
                                        0f,
                                        startY + (charIndex * letterSpacing),
                                        textPaint
                                    )
                                }

                                restore()
                            }
                        }

                        // Dibujar borde
                        drawCircle(
                            color = Color.White,
                            radius = radius,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 4f)
                        )

                        // Círculo central
                        drawCircle(
                            color = Color.Black,
                            radius = 40f,
                            center = Offset(centerX, centerY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 40f,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 3f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón girar
            Button(
                onClick = {
                    if (!isSpinning) {
                        isSpinning = true
                    }
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                enabled = !isSpinning
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(ClashPointsPurple, ClashPointsPink)
                            ),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSpinning) "Girando..." else "Girar Ruleta",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}