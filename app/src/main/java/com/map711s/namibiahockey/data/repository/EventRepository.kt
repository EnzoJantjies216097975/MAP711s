package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.local.PreferencesManager
import com.map711s.namibiahockey.data.local.dao.EventDao
import com.map711s.namibiahockey.data.models.*
import com.map711s.namibiahockey.data.remote.EventService
import com.map711s.namibiahockey.data.remote.FirebaseManager
import com.map711s.namibiahockey.util.NetworkBoundResource
import com.map711s.namibiahockey.util.Resource
import kotlinx.coroutines.flow.Flow
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

    // Other methods remain the same

    // Helper to check if data needs refreshing
    private fun isDataStale(): Boolean {
        val lastSync = preferencesManager.getLastSyncTimestamp()
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        return (currentTime - lastSync) > oneHourInMillis
    }

    // Fix: Replace viewModelScope with GlobalScope for repository class
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