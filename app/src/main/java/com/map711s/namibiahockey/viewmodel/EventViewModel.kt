package com.map711s.namibiahockey.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.GameResult
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.data.model.TeamSeasonStats
import com.map711s.namibiahockey.data.model.UserRole
import com.map711s.namibiahockey.data.repository.AuthRepository
import com.map711s.namibiahockey.data.repository.EventRepository
import com.map711s.namibiahockey.data.repository.GameResultsRepository
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
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository,
    private val teamRepository: TeamRepository,
    private val playerRepository: PlayerRepository,
    private val gameResultsRepository: GameResultsRepository

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

    private val TAG = "EventViewModel"

    // Single event for details
    private val _event = MutableStateFlow<EventEntry?>(null)
    val event: StateFlow<EventEntry?> = _event.asStateFlow()

    // Team selection state
    private val _showTeamSelection = MutableStateFlow(false)
    val showTeamSelection: StateFlow<Boolean> = _showTeamSelection.asStateFlow()

    private val _availableTeams = MutableStateFlow<List<Team>>(emptyList())
    val availableTeams: StateFlow<List<Team>> = _availableTeams.asStateFlow()

    private val _registrationMessage = MutableStateFlow<String?>(null)
    val registrationMessage: StateFlow<String?> = _registrationMessage.asStateFlow()

    // Game results
    private val _gameResults = MutableStateFlow<List<GameResult>>(emptyList())
    val gameResults: StateFlow<List<GameResult>> = _gameResults.asStateFlow()

    private val _teamStats = MutableStateFlow<List<TeamSeasonStats>>(emptyList())
    val teamStats: StateFlow<List<TeamSeasonStats>> = _teamStats.asStateFlow()

    // Currently pending registration event ID
    private var _pendingEventId: String? = null


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
                            eventId = eventId,
                            successMessage = "Event created successfully!"
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

    // Dismiss team selection dialog
    fun dismissTeamSelection() {
        _showTeamSelection.value = false
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

    // Load a specific event
    fun loadEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null, eventId = eventId) }

        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    _event.value = event
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            event = event,
                            isRegistered = event.isRegistered
                        )
                    }

                    // Load game results if it's a past event
                    if (isPastEvent(event)) {
                        loadGameResults(eventId)
                    }
                }
                .onFailure { exception ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load event"
                        )
                    }
                }
        }
    }

    // Check if user is registered for an event
    fun checkIfUserIsRegistered(eventId: String, userId: String) {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            try {
                val event = eventRepository.getEvent(eventId).getOrNull()
                val isRegistered = event?.registeredUserIds?.contains(userId) ?: false

                _registrationState.update {
                    it.copy(isRegistered = isRegistered)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking registration status", e)
            }
        }
    }

    // Reset states
    fun resetEventState() {
        _eventState.value = EventState()
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState()
    }

    // Update event
    fun updateEvent(event: EventEntry) {
        _eventState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            eventRepository.updateEvent(event)
                .onSuccess {
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Event updated successfully!"
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

    // Delete event
    fun deleteEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            eventRepository.deleteEvent(eventId)
                .onSuccess {
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessage = "Event deleted successfully!"
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

    // Initiate registration process
    fun initiateRegistration(eventId: String) {
        Log.d(TAG, "Initiating registration for event: $eventId")
        _pendingEventId = eventId

        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _registrationMessage.value = "You must be logged in to register for events"
                return@launch
            }

            // Get user profile to determine role
            val userProfile = authRepository.getUserProfile(userId).getOrNull()
            if (userProfile == null) {
                _registrationMessage.value = "Could not load user profile"
                return@launch
            }

            Log.d(TAG, "User role: ${userProfile.role}")

            when (userProfile.role) {
                UserRole.PLAYER -> handlePlayerRegistration(eventId, userId)
                UserRole.COACH -> handleCoachRegistration(eventId, userId)
                UserRole.MANAGER -> handleManagerRegistration(eventId, userId)
                UserRole.ADMIN -> handleAdminRegistration(eventId, userId)
            }
        }
    }

    private suspend fun handlePlayerRegistration(eventId: String, userId: String) {
        Log.d(TAG, "Handling player registration")

        // Get teams the player is part of
        val playerTeams = getUserTeams(userId).getOrNull() ?: emptyList()
        Log.d(TAG, "Player teams found: ${playerTeams.size}")

        if (playerTeams.isEmpty()) {
            _registrationMessage.value = "You must be part of a team to register for events"
            return
        }

        if (playerTeams.size == 1) {
            // Auto-register with the only team
            registerTeamForEvent(eventId, playerTeams.first().id)
        } else {
            // Show team selection dialog
            _availableTeams.value = playerTeams
            _showTeamSelection.value = true
        }
    }

    private suspend fun handleCoachRegistration(eventId: String, userId: String) {
        Log.d(TAG, "Handling coach registration")

        // Get teams the coach manages
        val coachTeams = getUserTeams(userId).getOrNull() ?: emptyList()
        Log.d(TAG, "Coach teams found: ${coachTeams.size}")

        if (coachTeams.isEmpty()) {
            _registrationMessage.value = "No teams found that you manage"
            return
        }

        if (coachTeams.size == 1) {
            // Auto-register with the only team
            registerTeamForEvent(eventId, coachTeams.first().id)
        } else {
            // Show team selection dialog
            _availableTeams.value = coachTeams
            _showTeamSelection.value = true
        }
    }

    private suspend fun handleManagerRegistration(eventId: String, userId: String) {
        Log.d(TAG, "Handling manager registration")
        // Same logic as coach
        handleCoachRegistration(eventId, userId)
    }

    private suspend fun handleAdminRegistration(eventId: String, userId: String) {
        Log.d(TAG, "Handling admin registration")

        // Admin can register any team
        val allTeams = teamRepository.getAllTeams().getOrNull() ?: emptyList()
        if (allTeams.isEmpty()) {
            _registrationMessage.value = "No teams available for registration"
            return
        }

        _availableTeams.value = allTeams
        _showTeamSelection.value = true
    }

    // Register team for event
    fun registerForEvent(eventId: String, teamId: String) {
        Log.d(TAG, "Registering team $teamId for event $eventId")
        registerTeamForEvent(eventId, teamId)
    }

    private fun registerTeamForEvent(eventId: String, teamId: String) {
        _registrationState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // Check for event conflicts first
                val event = eventRepository.getEvent(eventId).getOrNull()
                if (event != null) {
                    val conflicts = eventRepository.checkEventConflicts(teamId, event.startDate)
                    if (conflicts.isNotEmpty()) {
                        _eventState.update {
                            it.copy(conflictingEvents = conflicts)
                        }
                        _registrationMessage.value = "Warning: Team has conflicts on ${event.startDate}: ${conflicts.joinToString(", ")}"
                        // Still allow registration but warn user
                    }
                }

                eventRepository.registerForEvent(eventId, teamId)
                    .onSuccess {
                        _registrationState.update {
                            it.copy(
                                isLoading = false,
                                isSuccess = true,
                                isRegistered = true
                            )
                        }

                        _registrationMessage.value = "Successfully registered for event!"

                        // Update event state
                        updateEventRegistrationState(eventId, true)

                        // Hide team selection
                        _showTeamSelection.value = false

                        // Reload events to get updated counts
                        loadAllEvents()
                    }
                    .onFailure { exception ->
                        _registrationState.update {
                            it.copy(
                                isLoading = false,
                                error = exception.message
                            )
                        }
                        _registrationMessage.value = "Registration failed: ${exception.message}"
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Registration error", e)
                _registrationState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                _registrationMessage.value = "Registration failed: ${e.message}"
            }
        }
    }

    private fun updateEventRegistrationState(eventId: String, isRegistered: Boolean) {
        // Update single event
        _event.value?.let { currentEvent ->
            if (currentEvent.id == eventId) {
                val updatedEvent = currentEvent.copy(
                    isRegistered = isRegistered,
                    registeredTeams = if (isRegistered) {
                        currentEvent.registeredTeams + 1
                    } else {
                        maxOf(0, currentEvent.registeredTeams - 1)
                    }
                )
                _event.value = updatedEvent
                _eventState.update { it.copy(event = updatedEvent, isRegistered = isRegistered) }
            }
        }

        // Update events list
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
        }
    }

    // Load all events
    fun loadAllEvents() {
        _eventListState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            eventRepository.getAllEvents()
                .onSuccess { events ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            events = events,
                            upcomingEvents = events.filter { event -> !isPastEvent(event) },
                            pastEvents = events.filter { event -> isPastEvent(event) },
                            myRegisteredEvents = events.filter { event -> event.isRegistered }
                        )
                    }
                    Log.d(TAG, "Loaded ${events.size} events")
                }
                .onFailure { exception ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load events"
                        )
                    }
                    Log.e(TAG, "Error loading events: ${exception.message}")
                }
        }
    }

    // Load events by hockey type
    fun loadEventsByType(hockeyType: HockeyType) {
        _eventListState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            eventRepository.getEventsByType(hockeyType)
                .onSuccess { events ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            events = events,
                            upcomingEvents = events.filter { event -> !isPastEvent(event) },
                            pastEvents = events.filter { event -> isPastEvent(event) },
                            myRegisteredEvents = events.filter { event -> event.isRegistered }
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

    // Load game results for past events
    fun loadGameResults(eventId: String) {
        viewModelScope.launch {
            gameResultsRepository.getGameResultsForEvent(eventId)
                .onSuccess { results ->
                    _gameResults.value = results
                    _eventState.update { it.copy(gameResults = results) }
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading game results: ${exception.message}")
                }
        }
    }

    // Load team season statistics
    fun loadTeamStats(season: String = "2024") {
        viewModelScope.launch {
            gameResultsRepository.getTeamSeasonStats(season)
                .onSuccess { stats ->
                    _teamStats.value = stats
                    Log.d(TAG, "Loaded ${stats.size} team statistics")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading team stats: ${exception.message}")
                }
        }
    }

    // Get user teams based on role
    private suspend fun getUserTeams(userId: String): Result<List<Team>> {
        return eventRepository.getUserTeams(userId)
    }

    // Get registered events for current user
    fun getMyRegisteredEvents(): List<EventEntry> {
        return _eventListState.value.myRegisteredEvents
    }

    // Clear messages
    fun clearMessages() {
        _eventState.update {
            it.copy(error = null, successMessage = null)
        }
        _registrationState.update {
            it.copy(error = null, message = null)
        }
    }

    fun clearRegistrationMessage() {
        _registrationMessage.value = null
    }

    // Helper function to check if event is in the past
    private fun isPastEvent(event: EventEntry): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val eventEndDate = dateFormat.parse(event.endDate)
            val today = Date()
            eventEndDate?.before(today) == true
        } catch (e: Exception) {
            false
        }
    }

}
