package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.TeamStatistics
import com.map711s.namibiahockey.data.model.User
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.PlayerRepository
import com.map711s.namibiahockey.data.repository.PlayerRequestRepository
import com.map711s.namibiahockey.data.repository.StatisticsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val requestRepository: PlayerRequestRepository,
    private val statsRepository: StatisticsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _playersState = MutableStateFlow<List<Player>>(emptyList())
    val playersState: StateFlow<List<Player>> = _playersState.asStateFlow()

    private val _requestsState = MutableStateFlow<List<PlayerRequest>>(emptyList())
    val requestsState: StateFlow<List<PlayerRequest>> = _requestsState.asStateFlow()

    private val _teamStats = MutableStateFlow<TeamStatistics?>(null)
    val teamStats: StateFlow<TeamStatistics?> = _teamStats.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    /**
     * Load team statistics and current user profile.
     */
    fun loadTeamDetails(teamId: String) {
        viewModelScope.launch {
            // Fetch and emit team statistics
            try {
                val stats = statsRepository.getTeamStatistics(teamId)
                _teamStats.value = stats
            } catch (e: Exception) {
                // Handle error or keep null
            }

            // Fetch current user profile - FIXED: Use correct method name
            try {
                authRepository.getCurrentUserProfile()
                    .onSuccess { user ->
                        _userProfile.value = user
                    }
                    .onFailure { exception ->
                        // Handle error - user profile remains null
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Load players belonging to the team.
     */
    fun loadTeamPlayers(teamId: String) {
        viewModelScope.launch {
            // FIXED: Handle Result<List<Player>> return type properly
            playerRepository.getPlayersByTeam(teamId)
                .onSuccess { players ->
                    _playersState.value = players
                }
                .onFailure { exception ->
                    // Handle error - keep empty list
                    _playersState.value = emptyList()
                }
        }
    }

    /**
     * Load pending join requests if user is coach/manager/admin.
     */
    fun loadPendingRequests(teamId: String) {
        viewModelScope.launch {
            try {
                // Get pending requests
                val requests = requestRepository.getPendingRequests(teamId)
                _requestsState.value = requests
            } catch (e: Exception) {
                // Handle error
                _requestsState.value = emptyList()
            }
        }
    }

    /**
     * Approve a player join request.
     */
    fun approveRequest(requestId: String) {
        viewModelScope.launch {
            try {
                // Approve the request
                requestRepository.approveRequest(requestId)
                    .onSuccess {
                        // Refresh requests - get current team ID from team stats
                        val currentTeamId = _teamStats.value?.teamId ?: ""
                        if (currentTeamId.isNotEmpty()) {
                            val updatedRequests = requestRepository.getPendingRequests(currentTeamId)
                            _requestsState.value = updatedRequests
                        }
                    }
                    .onFailure { exception ->
                        // Handle error
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    /**
     * Reject a player join request.
     */
    fun rejectRequest(requestId: String) {
        viewModelScope.launch {
            try {
                // Reject the request
                requestRepository.rejectRequest(requestId)
                    .onSuccess {
                        // Refresh requests - get current team ID from team stats
                        val currentTeamId = _teamStats.value?.teamId ?: ""
                        if (currentTeamId.isNotEmpty()) {
                            val updatedRequests = requestRepository.getPendingRequests(currentTeamId)
                            _requestsState.value = updatedRequests
                        }
                    }
                    .onFailure { exception ->
                        // Handle error
                    }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}