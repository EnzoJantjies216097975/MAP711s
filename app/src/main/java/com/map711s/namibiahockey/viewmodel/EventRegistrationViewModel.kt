package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EventRegistrationViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val teamRepository: TeamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // FIXED: Add missing state properties
    private val _showTeamSelection = MutableStateFlow(false)
    val showTeamSelection: StateFlow<Boolean> = _showTeamSelection.asStateFlow()

    private val _availableTeams = MutableStateFlow<List<Team>>(emptyList())
    val availableTeams: StateFlow<List<Team>> = _availableTeams.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

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
                        _availableTeams.value = managedTeams
                        _showTeamSelection.value = true
                    }
                }
                UserRole.ADMIN -> {
                    // Admin can register any team
                    val allTeams = teamRepository.getAllTeams().getOrNull() ?: emptyList()
                    _availableTeams.value = allTeams
                    _showTeamSelection.value = true
                }
            }
        }
    }

    // FIXED: Add missing methods
    private suspend fun registerPlayerForEvent(eventId: String, userId: String, teamId: String) {
        _isLoading.value = true
        eventRepository.registerForEvent(eventId, teamId)
            .onSuccess {
                _successMessage.value = "Successfully registered for event!"
                _showTeamSelection.value = false
            }
            .onFailure { exception ->
                _errorMessage.value = "Registration failed: ${exception.message}"
            }
        _isLoading.value = false
    }

    private suspend fun registerTeamForEvent(eventId: String, teamId: String) {
        _isLoading.value = true
        eventRepository.registerForEvent(eventId, teamId)
            .onSuccess {
                _successMessage.value = "Team successfully registered for event!"
                _showTeamSelection.value = false
            }
            .onFailure { exception ->
                _errorMessage.value = "Registration failed: ${exception.message}"
            }
        _isLoading.value = false
    }

    private suspend fun getTeamsForUser(userId: String): List<Team> {
        return eventRepository.getUserTeams(userId).getOrNull() ?: emptyList()
    }

    private suspend fun checkEventClashes(eventId: String, teamId: String): Boolean {
        // Check if team has another event on same day
        val event = eventRepository.getEvent(eventId).getOrNull() ?: return false
        val eventsByTeam = getEventsByTeam(teamId)

        return eventsByTeam.any { otherEvent ->
            otherEvent.id != eventId &&
                    isSameDay(event.startDate, otherEvent.startDate)
        }
    }

    // FIXED: Add missing helper methods
    private suspend fun getEventsByTeam(teamId: String): List<EventEntry> {
        // Get all events where this team is registered
        return try {
            eventRepository.getAllEvents().getOrNull()?.filter { event ->
                // This would need to be implemented based on your registration structure
                event.registeredUserIds.isNotEmpty() // Placeholder logic
            } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun isSameDay(date1: String, date2: String): Boolean {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val d1 = formatter.parse(date1)
            val d2 = formatter.parse(date2)

            if (d1 != null && d2 != null) {
                val cal1 = java.util.Calendar.getInstance().apply { time = d1 }
                val cal2 = java.util.Calendar.getInstance().apply { time = d2 }

                cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                        cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun dismissTeamSelection() {
        _showTeamSelection.value = false
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}