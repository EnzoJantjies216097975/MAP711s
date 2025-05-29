package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.GameEvent
import com.map711s.namibiahockey.data.model.GameEventType
import com.map711s.namibiahockey.data.model.LiveGame
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.LiveGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LiveGameViewModel @Inject constructor(
    private val liveGameRepository: LiveGameRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _liveGames = MutableStateFlow<List<LiveGame>>(emptyList())
    val liveGames: StateFlow<List<LiveGame>> = _liveGames.asStateFlow()

    private val _gameState = MutableStateFlow(LiveGameState())
    val gameState: StateFlow<LiveGameState> = _gameState.asStateFlow()

    init {
        observeLiveGames()
    }

    private fun observeLiveGames() {
        viewModelScope.launch {
            liveGameRepository.observeLiveGames()
                .collect { games ->
                    _liveGames.value = games
                }
        }
    }

    fun createLiveGame(game: LiveGame) {
        _gameState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId == null) {
                _gameState.update { it.copy(isLoading = false, error = "Not authenticated") }
                return@launch
            }

            liveGameRepository.createLiveGame(game.copy(adminId = currentUserId))
                .onSuccess { gameId ->
                    _gameState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            gameId = gameId
                        )
                    }
                }
                .onFailure { exception ->
                    _gameState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to create live game"
                        )
                    }
                }
        }
    }

    fun updateScore(gameId: String, team1Score: Int, team2Score: Int) {
        viewModelScope.launch {
            liveGameRepository.updateGameScore(gameId, team1Score, team2Score)
                .onFailure { exception ->
                    _gameState.update {
                        it.copy(error = exception.message ?: "Failed to update score")
                    }
                }
        }
    }

    fun addGameEvent(gameId: String, eventType: GameEventType, description: String, playerId: String? = null, teamId: String = "") {
        viewModelScope.launch {
            val gameEvent = GameEvent(
                id = UUID.randomUUID().toString(),
                gameId = gameId,
                type = eventType,
                description = description,
                timestamp = Date(),
                playerId = playerId,
                teamId = teamId
            )

            liveGameRepository.addGameEvent(gameEvent)
                .onFailure { exception ->
                    _gameState.update {
                        it.copy(error = exception.message ?: "Failed to add game event")
                    }
                }
        }
    }

    fun endGame(gameId: String) {
        viewModelScope.launch {
            liveGameRepository.endGame(gameId)
                .onSuccess {
                    _gameState.update { it.copy(isSuccess = true) }
                }
                .onFailure { exception ->
                    _gameState.update {
                        it.copy(error = exception.message ?: "Failed to end game")
                    }
                }
        }
    }

    fun resetGameState() {
        _gameState.update { LiveGameState() }
    }
}

data class LiveGameState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val gameId: String? = null,
    val error: String? = null
)