package com.map711s.namibiahockey.ui.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.models.Team
import com.map711s.namibiahockey.data.models.TeamRequest
import com.map711s.namibiahockey.data.models.TeamWithPlayers
import com.map711s.namibiahockey.data.repository.TeamRepository
import com.map711s.namibiahockey.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val _teamOperationState = MutableStateFlow<Resource<Team>?>(null)
    val teamOperationState: StateFlow<Resource<Team>?> = _teamOperationState

    fun getTeamDetails(teamId: String): Flow<Resource<TeamWithPlayers>> {
        return teamRepository.getTeamWithPlayers(teamId)
    }

    fun registerTeam(teamRequest: TeamRequest, logoFile: File? = null) {
        viewModelScope.launch {
            _teamOperationState.value = Resource.Loading()

            val result = teamRepository.registerTeam(teamRequest, logoFile)
            _teamOperationState.value = result
        }
    }

    fun updateTeam(teamId: String, teamRequest: TeamRequest, logoFile: File? = null) {
        viewModelScope.launch {
            _teamOperationState.value = Resource.Loading()

            val result = teamRepository.updateTeam(teamId, teamRequest, logoFile)
            _teamOperationState.value = result
        }
    }

    fun clearOperationState() {
        _teamOperationState.value = null
    }
}