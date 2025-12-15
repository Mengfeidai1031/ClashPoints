package com.example.clashp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.clashp.navigation.AppNavigation
import com.example.clashp.ui.theme.ClashPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClashPTheme {
                AppNavigation()
            }
        }
    }
}
