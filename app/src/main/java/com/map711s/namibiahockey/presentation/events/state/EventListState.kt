package com.map711s.namibiahockey.presentation.events.state

import com.map711s.namibiahockey.data.model.EventEntry

// State for a list of EventEntry objects
data class EventListState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val events: List<EventEntry> = emptyList(),
    val canLoadMore: Boolean = false,
    val error: String? = null
)