package com.map711s.namibiahockey.presentation.events.state

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