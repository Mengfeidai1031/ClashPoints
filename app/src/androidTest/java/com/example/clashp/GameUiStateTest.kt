package com.example.clashp

import com.example.clashp.model.GameUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameUiStateTest {

    @Test
    fun totalQuestionsAnswered_isComputedCorrectly() {
        val state = GameUiState(currentRound = 2, questionsInCurrentRound = 3)
        assertEquals(7, state.totalQuestionsAnswered)
    }

    @Test
    fun shouldShowRoulette_trueOnlyWhenRoundStartsAndNotGameOver() {
        assertTrue(GameUiState(questionsInCurrentRound = 0, isGameOver = false).showRoulette)
        assertFalse(GameUiState(questionsInCurrentRound = 1, isGameOver = false).shouldShowRoulette)
        assertFalse(GameUiState(questionsInCurrentRound = 0, isGameOver = true).shouldShowRoulette)
    }
}