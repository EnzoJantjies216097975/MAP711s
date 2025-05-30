package com.map711s.namibiahockey.data.states

import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.GameResult
import com.map711s.namibiahockey.data.model.Team

// State for a single EventEntry
data class EventState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val event: EventEntry? = null,
    val eventId: String? = null,
    val error: String? = null,
    val isRegistered: Boolean = false,
    val successMessage: String? = null,
    val showTeamSelection: Boolean = false,
    val availableTeams: List<Team> = emptyList(),
    val conflictingEvents: List<String> = emptyList(),
    val gameResults: List<GameResult> = emptyList()
)

// State for a list of EventEntry objects
data class EventListState(
    val isLoading: Boolean = false,
    val events: List<EventEntry> = emptyList(),
    val error: String? = null,
    val myRegisteredEvents: List<EventEntry> = emptyList(),
    val pastEvents: List<EventEntry> = emptyList(),
    val upcomingEvents: List<EventEntry> = emptyList()
)

data class TeamSelectionState(
    val showTeamSelection: Boolean = false,
    val availableTeams: List<Team> = emptyList(),
    val eventId: String? = null
)

data class RegistrationState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val isRegistered: Boolean = false
)
