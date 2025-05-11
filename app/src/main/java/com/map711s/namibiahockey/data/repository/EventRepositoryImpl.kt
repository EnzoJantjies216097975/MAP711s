package com.map711s.namibiahockey.data.repository

import com.google.common.collect.Queues
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.map711s.namibiahockey.data.local.OfflineOperation
import com.map711s.namibiahockey.data.local.OfflineOperationQueue
import com.map711s.namibiahockey.data.local.OfflineOperationType
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.util.FirestorePaginator
import com.map711s.namibiahockey.util.NetworkMonitor
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val networkMonitor: NetworkMonitor,
    private val offlineQueues: OfflineOperationQueue
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

    override suspend fun getAllEvents(): Result<List<EventEntry>> {
        return try {
            // Always fetch from cache first for immediate UI response
            val querySnapshot = firestore.collection("events")
                .get(Source.CACHE)
                .await()

            val eventsFromCache = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(EventEntry::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            // If online, also fetch from server and update cache
            if (networkMonitor.isCurrentlyOnline()) {
                try {
                    val serverSnapshot = firestore.collection("events")
                        .get(Source.SERVER)
                        .await()

                    val eventsFromServer = serverSnapshot.documents.mapNotNull { document ->
                        try {
                            document.toObject(EventEntry::class.java)?.copy(id = document.id)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    // Return server data if available
                    return Result.success(eventsFromServer)
                } catch (e: Exception) {
                    // If server fetch fails, still return cache data
                }
            }

            // Return cache data
            Result.success(eventsFromCache)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}