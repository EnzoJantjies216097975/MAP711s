package com.map711s.namibiahockey.presentation.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.map711s.namibiahockey.presentation.events.state.EventState
import com.map711s.namibiahockey.presentation.events.state.EventListState


@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    // Event list state with pagination
    private val _eventListState = MutableStateFlow(
        EventListState(
            isLoading = false,
            events = emptyList(),
            canLoadMore = true,
            error = null
        )
    )

    val eventListState: StateFlow<EventListState> = _eventListState.asStateFlow()

    // Load first page of events
    fun loadEvents() {
        if (_eventListState.value.isLoading) return

        _eventListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getEventsFirstPage()
                .onSuccess { events ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            events = events,
                            canLoadMore = events.size == 10 // If we got a full page, there might be more
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

    // Load next page of events
    fun loadMoreEvents() {
        if (_eventListState.value.isLoading || !_eventListState.value.canLoadMore) return

        _eventListState.update { it.copy(isLoadingMore = true) }
        viewModelScope.launch {
            eventRepository.getEventsNextPage()
                .onSuccess { newEvents ->
                    _eventListState.update { currentState ->
                        currentState.copy(
                            isLoadingMore = false,
                            events = currentState.events + newEvents,
                            canLoadMore = newEvents.size == 10
                        )
                    }
                }
                .onFailure { exception ->
                    _eventListState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = exception.message ?: "Failed to load more events"
                        )
                    }
                }
        }
    }

    // Create a new event
    fun createEvent(event: EventEntry) {
        _eventState.update { it.copy(isLoading = true, error = null) }
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
        _eventState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            event = event
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

    // Update an existing event
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

    // Delete an event by ID
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

    // Load all events
    fun loadAllEvents() {
        _eventListState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.getAllEvents()
                .onSuccess { events ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            events = events
                        )
                    }
                    Log.i("it.NOerror", "loadAllEvents: ")

                }
                .onFailure { exception ->
                    _eventListState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load events"
                        )
                    }
                    Log.i("it.error", "loadAllEvents: ")
                }
        }
    }

    // Register for an event
    fun registerForEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.registerForEvent(eventId)
                .onSuccess {
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            isRegistered = true
                        )
                    }
                    loadAllEvents()
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

    // Unregister from an event
    fun unregisterFromEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            eventRepository.unregisterFromEvent(eventId)
                .onSuccess {
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            isRegistered = false
                        )
                    }
                    loadAllEvents()
                }
                .onFailure { exception ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to unregister from event"
                        )
                    }
                }
        }
    }
    // Reset event form
    fun resetEventState(){
        _eventState.update { EventState() }
    }
}