package com.example.clashp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clashp.ui.theme.ClashPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClashPTheme {
                Surface(color = Color.Black) {
                    ClashPointsHomeScreen(
                        onStartClick = { /* TODO: navegar a NameInput */ },
                        onRankingClick = { /* TODO: navegar a Ranking */ },
                        onSettingsClick = { /* TODO: abrir ajustes */ }
                    )
                }
            }
        }
    }
}

val gradient = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0A0A0A),  // Negro
        Color(0xFF1A0520),  // Morado oscuro
        Color(0xFF0D1B2A)   // Azul oscuro
    )
)

@Composable
fun ClashPointsHomeScreen(
    onStartClick: () -> Unit = {},  // esto es temporal
    onRankingClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.clashpointslogonotitle),
            contentDescription = "Logo ClashPoints",
            modifier = Modifier.size(160.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ClashPoints",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        PrimaryClashButton(text = "Start", onClick = onStartClick)
        Spacer(modifier = Modifier.height(16.dp))
        PrimaryClashButton(text = "Ranking", onClick = onRankingClick)

        Spacer(modifier = Modifier.height(24.dp))

        SecondaryClashButton(text = "Ajustes", onClick = onSettingsClick)
    }
}

@Composable
fun PrimaryClashButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF00FF), // fucsia
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 18.sp)
    }
}

@Composable
fun SecondaryClashButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.6f)
            .height(44.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF333333),
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ClashPointsPreview() {
    ClashPTheme {
        ClashPointsHomeScreen()
    }
}
