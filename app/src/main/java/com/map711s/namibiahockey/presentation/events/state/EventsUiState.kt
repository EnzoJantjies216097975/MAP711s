package com.map711s.namibiahockey.presentation.events.state

import com.map711s.namibiahockey.data.model.EventEntry

sealed class EventsUiState {
    object Loading : EventsUiState()
    data class Success(val events: List<EventEntry>) : EventsUiState()
    data class Error(val message: String, val isCritical: Boolean = false) : EventsUiState()
}