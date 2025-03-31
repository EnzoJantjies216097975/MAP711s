package com.map711s.namibiahockey.viewmodel

import android.adservices.adid.AdId
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.models.EventWithTeams
import com.map711s.namibiahockey.data.models.TeamSummary
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.TeamRepository
import com.map711s.namibiahockey.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val eventId: String = savedStateHandle.get<String>("eventId")
        ?: throw IllegalArgumentException("eventId is required")

    data class UiState(
        val event: EventWithTeams? = null,
        val userTeams: List<TeamSummary> = emptyList(),
        val selectedTeamId: String? = null,
        val isLoading: Boolean = true,
        val errorMessage: String? = null,
        val isRegistrationInProgress: Boolean = false,
        val isRegistrationDialogVisible: Boolean = false,
        val registrationComplete: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadEventDetails()
        loadUserTeams()
    }

    private fun loadEventDetails(){
        viewModelScope.launch {
            eventRepository.getEventWithTeams(eventId).collect{ result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                event = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                event = result.data,
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update {
                            it.copy(
                                event = result.data,
                                isLoading = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun loadUserTeams(){
        viewModelScope.launch {
            teamRepository.getUserTeams().collect { result ->
                when(result) {
                    is Resource.Success -> {
                        val teams = result.data
                        // If there's only one team, select it by default
                        val defaultTeamId = if (teams.size == 1) teams[0].id else null

                        _uiState.update {
                            it.copy(
                                userTeams = teams,
                                selectedTeamId = defaultTeamId
                            )
                        }
                    }
                    else ->{
                        // Ignore loading and error states here to keep the UI simple
                    }
                }
            }
        }
    }

    fun onTeamSelected(teamId: String){
        _uiState.update { it.copy(selectedTeamId = teamId) }
    }

    fun showRegistrationDialog() {
        _uiState.update { it.copy(isRegistrationDialogVisible = true) }
    }

    fun hideRegistrationDialog() {
        _uiState.update { it.copy(isRegistrationDialogVisible = false) }
    }

    fun registerForEvent(notes: String? = null) {
        val teamId = _uiState.value.selectedTeamId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isRegistrationInProgress = true) }

            val result = eventRepository.registerTeamForEvent(
                eventId = eventId,
                teamId = teamId,
                notes = notes
            )

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            isRegistrationInProgress = false,
                            isRegistrationDialogVisible = false,
                            registrationComplete = true,
                            errorMessage = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isRegistrationInProgress = false,
                            errorMessage = result.message
                        )
                    }
                }
                else -> { /* Should not happen */ }
            }
        }
    }
}