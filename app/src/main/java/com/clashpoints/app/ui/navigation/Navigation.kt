package com.clashpoints.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.clashpoints.app.ui.screens.MenuScreen
import com.clashpoints.app.ui.screens.NameInputScreen
import com.clashpoints.app.ui.screens.RouletteScreen
import com.clashpoints.app.ui.screens.QuestionScreen
import com.clashpoints.app.ui.screens.ResultScreen
import com.clashpoints.app.ui.screens.RankingScreen
import com.clashpoints.app.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object NameInput : Screen("name_input")
    object Roulette : Screen("roulette")
    object Question : Screen("question")
    object Result : Screen("result")
    object Ranking : Screen("ranking")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(Screen.Menu.route) {
            MenuScreen(
                onStartClick = {
                    gameViewModel.resetGame()  // Reset antes de nuevo juego
                    navController.navigate(Screen.NameInput.route)
                },
                onRankingClick = {
                    navController.navigate(Screen.Ranking.route)
                }
            )
        }

        composable(Screen.NameInput.route) {
            NameInputScreen(
                onNameSubmit = { name ->
                    gameViewModel.setPlayerName(name)
                    navController.navigate(Screen.Roulette.route)
                }
            )
        }

        composable(Screen.Roulette.route) {
            RouletteScreen(
                gameViewModel = gameViewModel,
                onCategorySelected = {
                    navController.navigate(Screen.Question.route)
                }
            )
        }

        composable(Screen.Question.route) {
            QuestionScreen(
                gameViewModel = gameViewModel,
                onRoundComplete = {
                    navController.navigate(Screen.Roulette.route)
                },
                onGameComplete = {
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                gameViewModel = gameViewModel,
                onSaveScore = {
                    navController.navigate(Screen.Ranking.route) {
                        popUpTo(Screen.Menu.route)
                    }
                },
                onBackToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Ranking.route) {
            RankingScreen(
                onBackToMenu = {
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.Menu.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}