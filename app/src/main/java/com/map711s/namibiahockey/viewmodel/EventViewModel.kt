package com.map711s.namibiahockey.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.PlayerRepository
import com.map711s.namibiahockey.data.repository.TeamRepository
import com.map711s.namibiahockey.data.states.EventListState
import com.map711s.namibiahockey.data.states.EventState
import com.map711s.namibiahockey.data.states.RegistrationState
import com.map711s.namibiahockey.data.states.TeamSelectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
    private val teamRepository: TeamRepository,
    private val playerRepository: PlayerRepository
) : ViewModel() {

    // Event creation/update state
    private val _eventState = MutableStateFlow(EventState())
    val eventState: StateFlow<EventState> = _eventState.asStateFlow()

    // Event list state
    private val _eventListState = MutableStateFlow(EventListState())
    val eventListState: StateFlow<EventListState> = _eventListState.asStateFlow()

    // Team selection state for player registration
    private val _teamSelectionState = MutableStateFlow(TeamSelectionState())
    val teamSelectionState: StateFlow<TeamSelectionState> = _teamSelectionState.asStateFlow()

    // Registration state
    private val _registrationState = MutableStateFlow(RegistrationState())
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // Create a new event
    fun createEvent(event: EventEntry) {
        _eventState.update { it.copy(isLoading = true, error = null) }

        if (authRepository.getCurrentUserId() == null) {
            _eventState.update {
                it.copy(
                    isLoading = false,
                    error = "You must be logged in to create events"
                )
            }
            return
        }

        viewModelScope.launch {
            eventRepository.createEvent(event)
                .onSuccess { eventId ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            eventId = eventId
                        )
                    }
                    loadAllEvents()
                }
                .onFailure { exception ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to create event"
                        )
                    }
                }
        }
    }

    // Get an event by ID
    fun getEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null, eventId = eventId) }
        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    // Check if current user is registered
                    val userId = authRepository.getCurrentUserId()
                    val isRegistered = userId?.let { event.registeredUserIds.contains(it) } ?: false

                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            event = event.copy(isRegistered = isRegistered),
                            isRegistered = isRegistered
                        )
                    }
                }
                .onFailure { exception ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to get event"
                        )
                    }
                }
        }
    }

    // Register for an event
    fun registerForEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null, eventId = eventId) }
        viewModelScope.launch {
            // Get current user ID
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _eventState.update {
                    it.copy(
                        isLoading = false,
                        error = "You must be logged in to register for events"
                    )
                }
                return@launch
            }

            // Find the event in the list
            val event = _eventListState.value.events.find { it.id == eventId }
            if (event == null) {
                _eventState.update {
                    it.copy(
                        isLoading = false,
                        error = "Event not found"
                    )
                }
                return@launch
            }

            if (event.isRegistered) {
                // If already registered, unregister
                eventRepository.unregisterFromEvent(eventId, userId)
                    .onSuccess {
                        // Update the event state
                        _eventState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isRegistered = false
                            )
                        }

                        // Update the event in the list
                            val updatedEvents = _eventListState.value.events.map { e ->
                                if (e.id == eventId) {
                                    e.copy(
                                        isRegistered = false,
                                        registeredTeams = maxOf(0, e.registeredTeams - 1)
                                    )
                                } else e
                            }
                            _eventListState.update { it.copy(events = updatedEvents) }

                    }
                    .onFailure { exception ->
                        _eventState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to unregister from event"
                            )
                        }
                    }
            } else {
                // If not registered, register
                eventRepository.registerForEvent(eventId, userId)
                    .onSuccess {
                        // Update the event state
                        _eventState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isRegistered = true
                            )
                        }

                        // Update the event in the list
                        val updatedEvents = _eventListState.value.events.map { e ->
                            if (e.id == eventId) {
                                e.copy(
                                    isRegistered = true,
                                    registeredTeams = e.registeredTeams + 1
                                )
                            } else e
                        }
                        _eventListState.update { it.copy(events = updatedEvents) }

                    }
                    .onFailure { exception ->
                        _eventState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message ?: "Failed to register for event"
                            )
                        }
                    }
            }
        }
    }

    // Register for an event - Enhanced with team selection
    fun registerForEvent(eventId: String, selectedTeamId: String? = null) {
        _registrationState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _registrationState.update {
                    it.copy(
                        isLoading = false,
                        error = "You must be logged in to register for events"
                    )
                }
                return@launch
            }

            try {
                // Get user profile to determine role
                val userProfile = authRepository.getUserProfile(userId).getOrNull()
                if (userProfile == null) {
                    _registrationState.update {
                        it.copy(
                            isLoading = false,
                            error = "Could not load user profile"
                        )
                    }
                    return@launch
                }

                // Get the event
                val event = eventRepository.getEvent(eventId).getOrNull()
                if (event == null) {
                    _registrationState.update {
                        it.copy(
                            isLoading = false,
                            error = "Event not found"
                        )
                    }
                    return@launch
                }

                // Check for event day clashes
                if (selectedTeamId != null) {
                    val hasClash = checkEventClashes(event, selectedTeamId)
                    if (hasClash) {
                        _registrationState.update {
                            it.copy(
                                isLoading = false,
                                error = "This team has another event on the same day!"
                            )
                        }
                        return@launch
                    }
                }

                when (userProfile.role) {
                    UserRole.PLAYER -> {
                        handlePlayerRegistration(eventId, userId, selectedTeamId)
                    }
                    UserRole.COACH -> {
                        handleCoachRegistration(eventId, userId, selectedTeamId)
                    }
                    UserRole.MANAGER -> {
                        handleManagerRegistration(eventId, userId, selectedTeamId)
                    }
                    UserRole.ADMIN -> {
                        handleAdminRegistration(eventId, userId, selectedTeamId)
                    }
                }
            } catch (e: Exception) {
                Log.e("EventViewModel", "Registration error", e)
                _registrationState.update {
                    it.copy(
                        isLoading = false,
                        error = "Registration failed: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun handlePlayerRegistration(eventId: String, userId: String, selectedTeamId: String?) {
        // Get player's teams
        val playerTeams = getPlayerTeams(userId)

        Log.d("EventViewModel", "Player teams: ${playerTeams.size}")

        if (playerTeams.isEmpty()) {
            _registrationState.update {
                it.copy(
                    isLoading = false,
                    error = "You must be part of a team to register for events"
                )
            }
            return
        }

        if (selectedTeamId == null) {
            // Show team selection dialog
            _teamSelectionState.update {
                it.copy(
                    showTeamSelection = true,
                    availableTeams = playerTeams,
                    eventId = eventId
                )
            }
            _registrationState.update { it.copy(isLoading = false) }
            return
        }

        // Register with selected team
        registerTeamForEvent(eventId, selectedTeamId, userId)
    }

    private suspend fun handleCoachRegistration(eventId: String, userId: String, selectedTeamId: String?) {
        // Get teams the coach manages
        val coachTeams = getCoachTeams(userId)

        Log.d("EventViewModel", "Coach teams: ${coachTeams.size}")

        if (coachTeams.isEmpty()) {
            _registrationState.update {
                it.copy(
                    isLoading = false,
                    error = "No teams found that you manage"
                )
            }
            return
        }

        if (selectedTeamId == null && coachTeams.size > 1) {
            // Show team selection dialog
            _teamSelectionState.update {
                it.copy(
                    showTeamSelection = true,
                    availableTeams = coachTeams,
                    eventId = eventId
                )
            }
            _registrationState.update { it.copy(isLoading = false) }
            return
        }

        // Use the only team or the selected team
        val teamToRegister = selectedTeamId ?: coachTeams.first().id
        registerTeamForEvent(eventId, teamToRegister, userId)
    }

    private suspend fun handleManagerRegistration(eventId: String, userId: String, selectedTeamId: String?) {
        // Similar to coach registration
        handleCoachRegistration(eventId, userId, selectedTeamId)
    }

    private suspend fun handleAdminRegistration(eventId: String, userId: String, selectedTeamId: String?) {
        // Admin can register any team
        if (selectedTeamId == null) {
            val allTeams = teamRepository.getAllTeams().getOrNull() ?: emptyList()
            _teamSelectionState.update {
                it.copy(
                    showTeamSelection = true,
                    availableTeams = allTeams,
                    eventId = eventId
                )
            }
            _registrationState.update { it.copy(isLoading = false) }
            return
        }

        registerTeamForEvent(eventId, selectedTeamId, userId)
    }

    private suspend fun registerTeamForEvent(eventId: String, teamId: String, userId: String) {
        try {
            eventRepository.registerForEvent(eventId, userId)
                .onSuccess {
                    // Update local state
                    _registrationState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            message = "Successfully registered for event!"
                        )
                    }

                    // Update the event in the list
                    updateEventInList(eventId, true)

                    // Hide team selection
                    _teamSelectionState.update { it.copy(showTeamSelection = false) }

                    Log.d("EventViewModel", "Successfully registered team $teamId for event $eventId")
                }
                .onFailure { exception ->
                    _registrationState.update {
                        it.copy(
                            isLoading = false,
                            error = "Registration failed: ${exception.message}"
                        )
                    }
                }
        } catch (e: Exception) {
            _registrationState.update {
                it.copy(
                    isLoading = false,
                    error = "Registration failed: ${e.message}"
                )
            }
        }
    }

    fun unregisterFromEvent(eventId: String) {
        _registrationState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _registrationState.update {
                    it.copy(
                        isLoading = false,
                        error = "You must be logged in to unregister from events"
                    )
                }
                return@launch
            }

            eventRepository.unregisterFromEvent(eventId, userId)
                .onSuccess {
                    _registrationState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            message = "Successfully unregistered from event"
                        )
                    }

                    // Update the event in the list
                    updateEventInList(eventId, false)

                    Log.d("EventViewModel", "Successfully unregistered from event $eventId")
                }
                .onFailure { exception ->
                    _registrationState.update {
                        it.copy(
                            isLoading = false,
                            error = "Unregistration failed: ${exception.message}"
                        )
                    }
                }
        }
    }

    private fun updateEventInList(eventId: String, isRegistered: Boolean) {
        val currentEvents = _eventListState.value.events.toMutableList()
        val eventIndex = currentEvents.indexOfFirst { it.id == eventId }

        if (eventIndex != -1) {
            val currentEvent = currentEvents[eventIndex]
            val updatedEvent = currentEvent.copy(
                isRegistered = isRegistered,
                registeredTeams = if (isRegistered) {
                    currentEvent.registeredTeams + 1
                } else {
                    maxOf(0, currentEvent.registeredTeams - 1)
                }
            )
            currentEvents[eventIndex] = updatedEvent

            _eventListState.update { it.copy(events = currentEvents) }

            // Also update the single event state if it matches
            if (_eventState.value.eventId == eventId) {
                _eventState.update {
                    it.copy(
                        event = updatedEvent,
                        isRegistered = isRegistered
                    )
                }
            }
        }
    }

    fun selectTeamForRegistration(teamId: String) {
        val eventId = _teamSelectionState.value.eventId
        if (eventId != null) {
            registerForEvent(eventId, teamId)
        }
    }

    fun dismissTeamSelection() {
        _teamSelectionState.update { it.copy(showTeamSelection = false) }
        _registrationState.update { it.copy(isLoading = false) }
    }

    private suspend fun getPlayerTeams(playerId: String): List<Team> {
        return try {
            val allTeams = teamRepository.getAllTeams().getOrNull() ?: emptyList()
            allTeams.filter { team -> team.players.contains(playerId) }
        } catch (e: Exception) {
            Log.e("EventViewModel", "Error getting player teams", e)
            emptyList()
        }
    }

    private suspend fun getCoachTeams(userId: String): List<Team> {
        return try {
            val userProfile = authRepository.getUserProfile(userId).getOrNull()
            val userName = userProfile?.name ?: ""

            val allTeams = teamRepository.getAllTeams().getOrNull() ?: emptyList()
            allTeams.filter { team ->
                team.coach.contains(userName, ignoreCase = true) ||
                        team.manager.contains(userName, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e("EventViewModel", "Error getting coach teams", e)
            emptyList()
        }
    }

    private suspend fun checkEventClashes(event: EventEntry, teamId: String): Boolean {
        return try {
            val allEvents = eventRepository.getAllEvents().getOrNull() ?: emptyList()
            val eventDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(event.startDate)

            allEvents.any { otherEvent ->
                if (otherEvent.id == event.id) return@any false

                val otherEventDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(otherEvent.startDate)
                val isSameDay = eventDate != null && otherEventDate != null &&
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(eventDate) ==
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(otherEventDate)

                // Check if the team is registered for the other event
                isSameDay && otherEvent.registeredUserIds.isNotEmpty()
            }
        } catch (e: Exception) {
            Log.e("EventViewModel", "Error checking event clashes", e)
            false
        }
    }

    // Load all events
    fun loadAllEvents() {
        _eventListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getAllEvents()
                .onSuccess { events ->
                    // Check registration status for current user
                    val userId = authRepository.getCurrentUserId()
                    val eventsWithRegistrationStatus = events.map { event ->
                        val isRegistered = userId?.let { event.registeredUserIds.contains(it) } ?: false
                        event.copy(isRegistered = isRegistered)
                    }

                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            events = eventsWithRegistrationStatus
                        )
                    }
                    Log.i("EventViewModel", "Loaded ${events.size} events")
                }
                .onFailure { exception ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load events"
                        )
                    }
                    Log.e("EventViewModel", "Error loading events: ${exception.message}")
                }
        }
    }

    // Load events by hockey type
    fun loadEventsByType(hockeyType: HockeyType) {
        _eventListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getEventsByType(hockeyType)
                .onSuccess { events ->
                    val userId = authRepository.getCurrentUserId()
                    val eventsWithRegistrationStatus = events.map { event ->
                        val isRegistered = userId?.let { event.registeredUserIds.contains(it) } ?: false
                        event.copy(isRegistered = isRegistered)
                    }

                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            events = eventsWithRegistrationStatus
                        )
                    }
                }
                .onFailure { exception ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load events"
                        )
                    }
                }
        }
    }

    // Reset states
    fun resetEventState() {
        _eventState.update { EventState() }
    }

    fun resetRegistrationState() {
        _registrationState.update { RegistrationState() }
    }

    fun updateEvent(event: EventEntry) {
        _eventState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.updateEvent(event)
                .onSuccess {
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    loadAllEvents()
                }
                .onFailure { exception ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to update event"
                        )
                    }
                }
        }
    }

    fun deleteEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
                .onSuccess {
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                    loadAllEvents()
                }
                .onFailure { exception ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to delete event"
                        )
                    }
                }
        }
    }
}