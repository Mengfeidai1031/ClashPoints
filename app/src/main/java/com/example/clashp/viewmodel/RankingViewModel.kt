package com.example.clashp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clashp.model.PlayerRanking
import com.example.clashp.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RankingUiState(
    val players: List<PlayerRanking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RankingViewModel(
    private val repository: RankingRepository = RankingRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState(isLoading = true))
    val uiState: StateFlow<RankingUiState> = _uiState.asStateFlow()

    init {
        loadRankings()
    }

    private fun loadRankings() {
        viewModelScope.launch {
            repository.getRankings().collect { result ->
                result.fold(
                    onSuccess = { rankings ->
                        _uiState.value = RankingUiState(
                            players = rankings,
                            isLoading = false
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = RankingUiState(
                            players = emptyList(),
                            isLoading = false,
                            error = exception.message ?: "Error al cargar el ranking"
                        )
                    }
                )
            }
        }
    }

    fun retry() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        loadRankings()
    }
}
