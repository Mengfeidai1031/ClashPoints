package com.example.clashp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clashp.view.HomeScreen
import com.example.clashp.view.RankingScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartClick = {
                    // TODO: Navegar a pantalla de juego
                    // navController.navigate(Screen.Game.route)
                },
                onRankingClick = {
                    navController.navigate(Screen.Ranking.route)
                },
                onSettingsClick = {
                    // TODO: Navegar a ajustes
                }
            )
        }

        composable(Screen.Ranking.route) {
            RankingScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Ranking : Screen("ranking")
    object Settings : Screen("settings")
    object Game : Screen("game")
}
