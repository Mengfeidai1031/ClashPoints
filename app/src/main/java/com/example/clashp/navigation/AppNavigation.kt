package com.example.clashp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clashp.view.GameScreen
import com.example.clashp.view.HomeScreen
import com.example.clashp.view.NameInputScreen
import com.example.clashp.view.RankingScreen
import com.example.clashp.view.ResultsScreen
import com.example.clashp.viewmodel.GameViewModel

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
                    navController.navigate(Screen.NameInput.route)
                },
                onRankingClick = {
                    navController.navigate(Screen.Ranking.route)
                },
                onSettingsClick = {
                    // TODO: Navegar a ajustes
                }
            )
        }

        composable(Screen.NameInput.route) {
            NameInputScreen(
                onStartGame = { playerName ->
                    navController.navigate(Screen.Game.createRoute(playerName))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("playerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            val gameViewModel: GameViewModel = viewModel()

            GameScreen(
                playerName = playerName,
                viewModel = gameViewModel,
                onGameFinished = { finalScore ->
                    navController.navigate(
                        Screen.Results.createRoute(playerName, finalScore)
                    ) {
                        // Eliminar GameScreen del back stack
                        popUpTo(Screen.Home.route)
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Results.route,
            arguments = listOf(
                navArgument("playerName") { type = NavType.StringType },
                navArgument("score") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val playerName = backStackEntry.arguments?.getString("playerName") ?: ""
            val score = backStackEntry.arguments?.getInt("score") ?: 0

            ResultsScreen(
                playerName = playerName,
                finalScore = score,
                onPlayAgain = {
                    navController.navigate(Screen.NameInput.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onGoHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onViewRanking = {
                    navController.navigate(Screen.Ranking.route) {
                        popUpTo(Screen.Home.route)
                    }
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
    object NameInput : Screen("name_input")
    object Game : Screen("game/{playerName}") {
        fun createRoute(playerName: String) = "game/$playerName"
    }
    object Results : Screen("results/{playerName}/{score}") {
        fun createRoute(playerName: String, score: Int) = "results/$playerName/$score"
    }
    object Ranking : Screen("ranking")
    object Settings : Screen("settings")
}