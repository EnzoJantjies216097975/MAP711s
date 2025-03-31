package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.local.PreferencesManager
import com.map711s.namibiahockey.data.local.dao.EventDao
import com.map711s.namibiahockey.data.models.*
import com.map711s.namibiahockey.data.remote.EventService
import com.map711s.namibiahockey.data.remote.FirebaseManager
import com.map711s.namibiahockey.services.HockeyMessagingService
import com.map711s.namibiahockey.util.NetworkBoundResource
import com.map711s.namibiahockey.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing event data
 */
@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val eventService: EventService,
    private val firebaseManager: FirebaseManager,
    private val preferencesManager: PreferencesManager
) {
    // Get all events
    fun getAllEvents(): Flow<Resource<List<EventListItem>>> {
        return NetworkBoundResource(
            query = {
                eventDao.getAllEventListItems()
            },
            fetch = {
                eventService.getAllEvents()
            },
            saveFetchResult = { events ->
                eventDao.insertEventListItems(events)
            },
            shouldFetch = { events ->
                events.isNullOrEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get upcoming events
    fun getUpcomingEvents(): Flow<Resource<List<EventListItem>>> {
        return NetworkBoundResource(
            query = {
                eventDao.getUpcomingEventListItems()
            },
            fetch = {
                eventService.getUpcomingEvents()
            },
            saveFetchResult = { events ->
                eventDao.insertEventListItems(events)
            },
            shouldFetch = { events ->
                events.isNullOrEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get past events
    fun getPastEvents(): Flow<Resource<List<EventListItem>>> {
        return NetworkBoundResource(
            query = {
                eventDao.getPastEventListItems()
            },
            fetch = {
                eventService.getPastEvents()
            },
            saveFetchResult = { events ->
                eventDao.insertEventListItems(events)
            },
            shouldFetch = { events ->
                events.isNullOrEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get user's registered events
    fun getUserEvents(): Flow<Resource<List<EventListItem>>> {
        val userId = preferencesManager.userId.value ?: return flow {
            emit(Resource.Error("User not logged in"))
        }

        return NetworkBoundResource(
            query = {
                eventDao.getUserEventListItems(userId)
            },
            fetch = {
                eventService.getUserEvents()
            },
            saveFetchResult = { events ->
                eventDao.insertUserEventListItems(events, userId)
            },
            shouldFetch = { events ->
                events.isNullOrEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get event with teams
    fun getEventWithTeams(eventId: String): Flow<Resource<EventWithTeams>> {
        return NetworkBoundResource(
            query = {
                eventDao.getEventWithTeams(eventId)
            },
            fetch = {
                eventService.getEventWithTeams(eventId)
            },
            saveFetchResult = { eventWithTeams ->
                eventDao.insertEventWithTeams(eventWithTeams)
            },
            shouldFetch = { event ->
                event == null || isDataStale()
            }
        ).asFlow()
    }

    // Get event matches
    fun getEventMatches(eventId: String): Flow<Resource<List<MatchSummary>>> {
        return NetworkBoundResource(
            query = {
                eventDao.getEventMatches(eventId)
            },
            fetch = {
                eventService.getEventMatches(eventId)
            },
            saveFetchResult = { matches ->
                eventDao.insertMatchSummaries(matches, eventId)
            },
            shouldFetch = { matches ->
                matches.isNullOrEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Create a new event
    suspend fun createEvent(
        eventRequest: EventRequest,
        imageFile: File? = null
    ): Resource<Event> {
        return try {
            // First, create the event
            val event = eventService.createEvent(eventRequest)

            // If image is provided, upload it
            if (imageFile != null) {
                val updatedEvent = eventService.uploadEventImage(event.id, imageFile)
                eventDao.insertEvent(updatedEvent)
                Resource.Success(updatedEvent)
            } else {
                eventDao.insertEvent(event)
                Resource.Success(event)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create event")
        }
    }

    // Update event
    suspend fun updateEvent(
        eventId: String,
        eventRequest: EventRequest,
        imageFile: File? = null
    ): Resource<Event> {
        return try {
            // Update event info
            val event = eventService.updateEvent(eventId, eventRequest)

            // If image is provided, upload it
            if (imageFile != null) {
                val updatedEvent = eventService.uploadEventImage(eventId, imageFile)
                eventDao.updateEvent(updatedEvent)
                Resource.Success(updatedEvent)
            } else {
                eventDao.updateEvent(event)
                Resource.Success(event)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update event")
        }
    }

    // Register team for event
    suspend fun registerTeamForEvent(
        eventId: String,
        teamId: String,
        notes: String? = null
    ): Resource<EventRegistration> {
        return try {
            val userId = preferencesManager.userId.value ?: return Resource.Error("User not logged in")

            val registrationRequest = EventRegistrationRequest(
                eventId = eventId,
                teamId = teamId,
                notes = notes
            )

            val registration = eventService.registerTeamForEvent(registrationRequest)

            // Update local database
            eventDao.insertEventRegistration(registration)

            // Mark event as registered for this user
            eventDao.markEventAsRegistered(eventId, userId)

            Resource.Success(registration)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to register team for event")
        }
    }

    // Cancel team registration for event
    suspend fun cancelEventRegistration(eventId: String, teamId: String): Resource<Unit> {
        return try {
            val userId = preferencesManager.userId.value ?: return Resource.Error("User not logged in")

            eventService.cancelEventRegistration(eventId, teamId)

            // Update local database
            eventDao.deleteEventRegistration(eventId, teamId)

            // If this was the only team registered by this user, mark event as unregistered
            val registrations = eventDao.getUserEventRegistrations(eventId, userId)
            if (registrations.isEmpty()) {
                eventDao.markEventAsUnregistered(eventId, userId)
            }

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel event registration")
        }
    }

    // Create match
    suspend fun createMatch(matchRequest: MatchRequest): Resource<Match> {
        return try {
            val match = eventService.createMatch(matchRequest)
            eventDao.insertMatch(match)
            Resource.Success(match)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create match")
        }
    }

    // Update match result
    suspend fun updateMatchResult(matchId: String, resultRequest: MatchResultRequest): Resource<Match> {
        return try {
            val match = eventService.updateMatchResult(matchId, resultRequest)
            eventDao.updateMatch(match)
            Resource.Success(match)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update match result")
        }
    }

    // Filter events by type
    fun getEventsByType(type: EventType): Flow<Resource<List<EventListItem>>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Get from local database first
                val localEvents = eventDao.getEventsByType(type)
                emit(Resource.Success(localEvents))

                // Try to fetch from network
                try {
                    val remoteEvents = eventService.getEventsByType(type)
                    eventDao.insertEventListItems(remoteEvents)
                    emit(Resource.Success(remoteEvents))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to get events by type"))
            }
        }
    }

    // Search events
    fun searchEvents(query: String): Flow<Resource<List<EventListItem>>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Search in local database first
                val localResults = eventDao.searchEvents("%$query%")
                emit(Resource.Success(localResults))

                // Try to search from network
                try {
                    val remoteResults = eventService.searchEvents(query)
                    eventDao.insertEventListItems(remoteResults)
                    emit(Resource.Success(remoteResults))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to search events"))
            }
        }
    }

    // Get events by date range
    fun getEventsByDateRange(startDate: Long, endDate: Long): Flow<Resource<List<EventListItem>>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Get from local database first
                val localEvents = eventDao.getEventsByDateRange(startDate, endDate)
                emit(Resource.Success(localEvents))

                // Try to fetch from network
                try {
                    val remoteEvents = eventService.getEventsByDateRange(startDate, endDate)
                    eventDao.insertEventListItems(remoteEvents)
                    emit(Resource.Success(remoteEvents))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to get events by date range"))
            }
        }
    }

    // Helper to check if data needs refreshing
    private fun isDataStale(): Boolean {
        val lastSync = preferencesManager.getLastSyncTimestamp()
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        return (currentTime - lastSync) > oneHourInMillis
    }

    // Add real-time events flow
    fun getEventsRealtime(): Flow<Resource<List<EventListItem>>> = flow {
        emit(Resource.Loading())

        try {
            // Emit from database first
            val localData = eventDao.getAllEventListItems().first()
            emit(Resource.Loading(localData))

            // Start listening for real-time updates
            firebaseManager.getEventsRealtime().collect { events ->
                // Convert to UI model
                val eventItems = events.map { event ->
                    // Convert Event to EventListItem
                    EventListItem(
                        id = event.id,
                        title = event.title,
                        description = event.description,
                        type = event.type,
                        startDate = event.startDate,
                        endDate = event.endDate,
                        location = event.location,
                        isRegistrationOpen = event.isRegistrationOpen,
                        teamCount = 0, // Calculate if needed
                        imageUrl = event.imageUrl,
                        status = event.status,
                        isUserRegistered = false // Update based on user data
                    )
                }

                // Save to database for offline access
                eventDao.insertEventListItems(eventItems)

                // Emit the updated data
                emit(Resource.Success(eventItems))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load events: ${e.message}"))
        }
    }

    suspend fun syncAllEvents() = viewModelScope.launch {
        try {
            val events = eventService.getAllEvents()
            eventDao.insertEventListItems(events)
            preferencesManager.updateLastSyncTimestamp()
        } catch (e: Exception) {
            // Handle error
        }
    }
}

