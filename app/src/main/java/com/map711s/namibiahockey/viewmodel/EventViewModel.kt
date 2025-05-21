package com.map711s.namibiahockey.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    // Event creation/update state
    private val _eventState = MutableStateFlow(EventState())
    val eventState: StateFlow<EventState> = _eventState.asStateFlow()

    // Event list state
    private val _eventListState = MutableStateFlow(EventListState())
    val eventListState: StateFlow<EventListState> = _eventListState.asStateFlow()

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
        _eventState.update { it.copy(isLoading = true, error = null, eventId = eventId) }
        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { event ->
                    _eventState.update {
                        it.copy(
                            isLoading = false,
                            event = event,
                            isRegistered = event.isRegistered
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

    // Register for an event
    fun registerForEvent(eventId: String) {
        _eventState.update { it.copy(isLoading = true, error = null, eventId = eventId) }
        viewModelScope.launch {
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
                eventRepository.unregisterFromEvent(eventId)
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
                        _eventListState.update { state ->
                            val updatedEvents = state.events.map { e ->
                                if (e.id == eventId) {
                                    e.copy(
                                        isRegistered = false,
                                        registeredTeams = maxOf(0, e.registeredTeams - 1)
                                    )
                                } else e
                            }
                            state.copy(events = updatedEvents)
                        }
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
                eventRepository.registerForEvent(eventId)
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
                        _eventListState.update { state ->
                            val updatedEvents = state.events.map { e ->
                                if (e.id == eventId) {
                                    e.copy(
                                        isRegistered = true,
                                        registeredTeams = e.registeredTeams + 1
                                    )
                                } else e
                            }
                            state.copy(events = updatedEvents)
                        }
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

    // Reset event form
    fun resetEventState() {
        _eventState.update { EventState() }
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
                            events = events
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
}