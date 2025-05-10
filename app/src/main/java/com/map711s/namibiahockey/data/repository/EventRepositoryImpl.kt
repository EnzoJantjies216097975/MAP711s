package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.map711s.namibiahockey.data.model.EventEntry
import com.map711s.namibiahockey.util.FirestorePaginator
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
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
}