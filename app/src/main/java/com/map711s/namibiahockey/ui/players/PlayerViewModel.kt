package com.map711s.namibiahockey.ui.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.models.Player
import com.map711s.namibiahockey.data.models.PlayerRequest
import com.map711s.namibiahockey.data.models.PlayerWithDetails
import com.map711s.namibiahockey.data.models.TeamPlayer
import com.map711s.namibiahockey.data.repository.PlayerRepository
import com.map711s.namibiahockey.data.repository.TeamRepository
import com.map711s.namibiahockey.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val _playerOperationState = MutableStateFlow<Resource<Player>?>(null)
    val playerOperationState: StateFlow<Resource<Player>?> = _playerOperationState

    fun getPlayerDetails(playerId: String): Flow<Resource<PlayerWithDetails>> {
        return playerRepository.getPlayerWithDetails(playerId)
    }

    fun getPlayerTeamDetails(playerId: String, teamId: String): Flow<Resource<TeamPlayer>> {
        // You'll need to add this method to your repository
        return flow {
            emit(Resource.Loading())
            try {
                // This is a simplified example - you'll need to implement the actual repository method
                val teamPlayer = teamRepository.getTeamPlayer(teamId, playerId)
                emit(Resource.Success(teamPlayer))
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Error getting team player details"))
            }
        }
    }

    fun registerPlayer(
        playerRequest: PlayerRequest,
        teamId: String? = null,
        jerseyNumber: Int? = null,
        position: String? = null,
        photoFile: File? = null
    ) {
        viewModelScope.launch {
            _playerOperationState.value = Resource.Loading()

            val result = playerRepository.registerPlayer(playerRequest, teamId, jerseyNumber, position, photoFile)
            _playerOperationState.value = result
        }
    }

    fun updatePlayer(
        playerId: String,
        playerRequest: PlayerRequest,
        teamId: String? = null,
        jerseyNumber: Int? = null,
        position: String? = null,
        photoFile: File? = null
    ) {
        viewModelScope.launch {
            _playerOperationState.value = Resource.Loading()

            // First update the player's basic information
            val playerResult = playerRepository.updatePlayer(playerId, playerRequest, photoFile)

            // If successful and team info is provided, update the team association
            if (playerResult is Resource.Success && teamId != null) {
                teamRepository.updatePlayerTeamRole(teamId, playerId, jerseyNumber, position)
            }

            _playerOperationState.value = playerResult
        }
    }

    fun clearOperationState() {
        _playerOperationState.value = null
    }
}