package com.example.clashp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.clashp.R
import com.example.clashp.ui.components.PrimaryClashButton
import com.example.clashp.ui.components.SecondaryClashButton
import com.example.clashp.ui.theme.ClashPTheme

@Composable
fun HomeScreen(
    onStartClick: () -> Unit = {},
    onRankingClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0A0A0A),
            Color(0xFF1A0520),
            Color(0xFF0D1B2A)
        )
    )

    Surface(color = Color.Black) {
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

            PrimaryClashButton(
                text = "Empezar",
                onClick = onStartClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryClashButton(
                text = "Ranking",
                onClick = onRankingClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            SecondaryClashButton(
                text = "Cuenta",
                onClick = onLoginClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ClashPTheme {
        HomeScreen()
    }
}
