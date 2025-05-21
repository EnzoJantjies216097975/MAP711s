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

//    init {
//        loadAllEvents()
//    }

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

    // Add this method to EventViewModel.kt
    suspend fun loadEventsByType(hockeyType: HockeyType) {
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