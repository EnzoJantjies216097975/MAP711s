package com.map711s.namibiahockey.viewmodel

import com.map711s.namibiahockey.data.model.EventEntry

// State for a single EventEntry
data class EventState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val event: EventEntry? = null,
    val eventId: String? = null,
    val error: String? = null,
    val isRegistered: Boolean = false
)

// State for a list of EventEntry objects
data class EventListState(
    val isLoading: Boolean = false,
    val events: List<EventEntry> = emptyList(),
    val error: String? = null
)