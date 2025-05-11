package com.map711s.namibiahockey.presentation.team

import com.map711s.namibiahockey.presentation.team.state.TeamListState
import com.map711s.namibiahockey.presentation.team.state.TeamState
import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository
) : ViewModel() {

    // Team creation/update state
    private val _teamState = MutableStateFlow(TeamState())
    val teamState: StateFlow<TeamState> = _teamState.asStateFlow()

    // Team list state
    private val _teamListState = MutableStateFlow(TeamListState())
    val teamListState: StateFlow<TeamListState> = _teamListState.asStateFlow()

    init {
        loadAllTeams()
    }

    // Create a new team
    fun createTeam(team: Team) {
        _teamState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            teamRepository.createTeam(team)
                .onSuccess { teamId ->
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            teamId = teamId
                        )
                    }
                    loadAllTeams()
                }
                .onFailure { exception ->
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to create team"
                        )
                    }
                }
        }
    }

    // Get a team by ID
    fun getTeam(teamId: String) {
        _teamState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            teamRepository.getTeam(teamId)
                .onSuccess { team ->
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            team = team
                        )
                    }
                }
                .onFailure { exception ->
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to get team"
                        )
                    }
                }
        }
    }

    // Update an existing team
    fun updateTeam(team: Team) {
        _teamState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            teamRepository.updateTeam(team)
                .onSuccess {
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    loadAllTeams()
                }
                .onFailure { exception ->
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to update team"
                        )
                    }
                }
        }
    }

    // Delete a team by ID
    fun deleteTeam(teamId: String) {
        _teamState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId)
                .onSuccess {
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    loadAllTeams()
                }
                .onFailure { exception ->
                    _teamState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to delete team"
                        )
                    }
                }
        }
    }

    // Load all teams
    fun loadAllTeams() {
        _teamListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            teamRepository.getAllTeams()
                .onSuccess { teams ->
                    _teamListState.update {
                        it.copy(
                            isLoading = false,
                            teams = teams
                        )
                    }
                }
                .onFailure { exception ->
                    _teamListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load teams"
                        )
                    }
                }
        }
    }
    // Reset team form
    fun resetTeamState(){
        _teamState.update { TeamState() }
    }
}