package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.local.PreferencesManager
import com.map711s.namibiahockey.data.local.dao.EventDao
import com.map711s.namibiahockey.data.models.*
import com.map711s.namibiahockey.data.remote.EventService
import com.map711s.namibiahockey.data.remote.FirebaseManager
import com.map711s.namibiahockey.util.NetworkBoundResource
import com.map711s.namibiahockey.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
                events.isEmpty() || isDataStale()
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
                events.isEmpty() || isDataStale()
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
                events.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get user events
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
                events.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get event with teams
    fun getEventWithTeams(eventId: String): Flow<Resource<EventWithTeams>> {
        return NetworkBoundResource(
            query = {
                eventDao.getEventWithTeams(eventId) ?: EventWithTeams(Event(
                    id = "",
                    title = "",
                    type = EventType.OTHER,
                    startDate = 0,
                    endDate = 0,
                    location = ""
                ))
            },
            fetch = {
                eventService.getEventWithTeams(eventId)
            },
            saveFetchResult = { eventWithTeams ->
                eventDao.insertEventWithTeams(eventWithTeams)
            },
            shouldFetch = { event ->
                event.event.id.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Register team for event
    suspend fun registerTeamForEvent(
        eventId: String,
        teamId: String,
        notes: String? = null
    ): Resource<EventRegistration> {
        return try {
            val userId = preferencesManager.userId.value
                ?: return Resource.Error("User not logged in")

            val request = EventRegistrationRequest(
                eventId = eventId,
                teamId = teamId,
                notes = notes
            )

            val registration = eventService.registerTeamForEvent(request)

            // Save to local database
            eventDao.insertEventRegistration(registration)
            eventDao.markEventAsRegistered(eventId, userId)

            Resource.Success(registration)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to register for event")
        }
    }

    // Create event (for organizers)
    suspend fun createEvent(eventRequest: EventRequest): Resource<Event> {
        return try {
            val event = eventService.createEvent(eventRequest)
            eventDao.insertEvent(event)
            Resource.Success(event)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create event")
        }
    }

    // Cancel event registration
    suspend fun cancelEventRegistration(eventId: String, teamId: String): Resource<Unit> {
        return try {
            val userId = preferencesManager.userId.value
                ?: return Resource.Error("User not logged in")

            eventService.cancelEventRegistration(eventId, teamId)
            eventDao.deleteEventRegistration(eventId, teamId)
            eventDao.markEventAsUnregistered(eventId, userId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to cancel registration")
        }
    }

    // Helper to check if data needs refreshing
    private fun isDataStale(): Boolean {
        val lastSync = preferencesManager.getLastSyncTimestamp()
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        return (currentTime - lastSync) > oneHourInMillis
    }

    // Sync all events - used by DataSyncWorker
    suspend fun syncAllEvents() {
        try {
            val events = eventService.getAllEvents()
            eventDao.insertEventListItems(events)
            preferencesManager.updateLastSyncTimestamp()
        } catch (e: Exception) {
            // Handle error
        }
    }
}