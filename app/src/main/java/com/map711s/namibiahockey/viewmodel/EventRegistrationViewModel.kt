package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventRegistrationViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    fun registerForEvent(eventId: String, selectedTeamId: String? = null) {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUserId() ?: return@launch
            val userProfile = authRepository.getUserProfile(currentUser).getOrNull() ?: return@launch

            when (userProfile.role) {
                UserRole.PLAYER -> {
                    if (selectedTeamId == null) {
                        // Show team selection dialog
                        _showTeamSelection.value = true
                        return@launch
                    }
                    registerPlayerForEvent(eventId, currentUser, selectedTeamId)
                }
                UserRole.COACH, UserRole.MANAGER -> {
                    val managedTeams = getTeamsForUser(currentUser)
                    if (managedTeams.size == 1) {
                        registerTeamForEvent(eventId, managedTeams.first().id)
                    } else {
                        // Show team selection for multiple teams
                        _showTeamSelection.value = true
                    }
                }
                UserRole.ADMIN -> {
                    // Admin can register any team
                    _showTeamSelection.value = true
                }
            }
        }
    }

    private suspend fun checkEventClashes(eventId: String, teamId: String): Boolean {
        // Check if team has another event on same day
        val event = eventRepository.getEvent(eventId).getOrNull() ?: return false
        val teamEvents = eventRepository.getEventsByTeam(teamId).getOrNull() ?: emptyList()

        return teamEvents.any { otherEvent ->
            otherEvent.id != eventId &&
                    isSameDay(event.startDate, otherEvent.startDate)
        }
    }
}