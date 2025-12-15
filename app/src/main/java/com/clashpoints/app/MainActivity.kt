package com.clashpoints.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.clashpoints.app.ui.navigation.AppNavigation
import com.clashpoints.app.ui.theme.ClashPointsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d("MainActivity", "Starting MainActivity")
            
            setContent {
                ClashPointsTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
            
            Log.d("MainActivity", "MainActivity initialized successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in MainActivity", e)
            e.printStackTrace()
        }
    }
}
