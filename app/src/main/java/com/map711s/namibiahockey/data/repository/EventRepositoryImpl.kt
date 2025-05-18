package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.map711s.namibiahockey.data.local.OfflineOperation
import com.map711s.namibiahockey.data.local.OfflineOperationQueue
import com.map711s.namibiahockey.data.local.OfflineOperationType
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.domain.repository.EventRepository
import com.map711s.namibiahockey.util.FirestorePaginator
import com.map711s.namibiahockey.util.NetworkMonitor
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context

@Singleton
class EventRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val offlineQueue: OfflineOperationQueue,
    @ApplicationContext private val context: Context
) : EventRepository {

    private val paginator by lazy {
        FirestorePaginator(
            baseQuery = firestore.collection("events")
                .orderBy("startDate", Query.Direction.DESCENDING),
            pageSize = 10
        ) { documentSnapshot ->
            documentSnapshot.toObject(EventEntry::class.java)?.copy(id = documentSnapshot.id)
        }
    }

    // Add pagination methods
    suspend fun getEventsFirstPage(): Result<List<EventEntry>> {
        return try {
            val events = paginator.loadFirstPage()
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEventsNextPage(): Result<List<EventEntry>> {
        return try {
            val events = paginator.loadNextPage()
            Result.success(events)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createEvent(event: EventEntry): Result<String> {
        // Check if device is online
        if (!networkMonitor.isCurrentlyOnline()) {
            // Queue the operation for later
            val operationId = UUID.randomUUID().toString()
            offlineQueue.enqueueOperation(
                OfflineOperation(
                    id = operationId,
                    type = OfflineOperationType.CREATE_EVENT,
                    data = event,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Return tentative success with local ID
            return Result.success("local_${operationId}")
        }

        // Online path - create directly
        return try {
            val documentReference = firestore.collection("events").add(event.toHashMap()).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getEvent(eventId: String): Result<EventEntry> {
        return try {
            val documentSnapshot = firestore.collection("events").document(eventId).get().await()
            if (documentSnapshot.exists()) {
                val event = documentSnapshot.toObject(EventEntry::class.java)
                    ?: return Result.failure(Exception("Failed to parse event data"))
                Result.success(event)
            } else {
                Result.failure(Exception("Event not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEvent(event: EventEntry): Result<Unit> {
        return try {
            firestore.collection("events").document(event.id).set(event.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            firestore.collection("events").document(eventId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getAllEvents(): Result<List<EventEntry>> {
        try {
            // Always fetch from cache first for immediate UI response
            val querySnapshot = firestore.collection("events")
                .get() // Use default source which tries cache first
                .await()

            val eventsFromSource = querySnapshot.documents

            if (eventsFromSource.isEmpty()) {
                return Result.success(emptyList())
            }

            val mappedEvents: List<EventEntry> = eventsFromSource.mapNotNull { document ->
                try {
                    val event = document.toObject(EventEntry::class.java)
                    event?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            return Result.success(mappedEvents)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun registerForEvent(eventId: String): Result<Unit> {
        return try {
            val eventResult = getEvent(eventId)
            if (eventResult.isSuccess) {
                val event = eventResult.getOrThrow()
                val updatedEvent = event.copy(isRegistered = true, registeredTeams = event.registeredTeams + 1)
                updateEvent(updatedEvent)
            } else {
                Result.failure(eventResult.exceptionOrNull() ?: Exception("Failed to get event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unregisterFromEvent(eventId: String): Result<Unit> {
        return try {
            val eventResult = getEvent(eventId)
            if (eventResult.isSuccess) {
                val event = eventResult.getOrThrow()
                val updatedEvent = event.copy(isRegistered = false, registeredTeams = event.registeredTeams - 1)
                updateEvent(updatedEvent)
            } else {
                Result.failure(eventResult.exceptionOrNull() ?: Exception("Failed to get event"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
