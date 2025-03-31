package com.map711s.namibiahockey.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.map711s.namibiahockey.data.models.Event
import com.map711s.namibiahockey.data.models.Player
import com.map711s.namibiahockey.data.models.Team
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val listenerRegistrations = mutableListOf<ListenerRegistration>()

    // Get real-time events updates
    fun getEventsRealtime(): Flow<List<Event>> = callbackFlow {
        val listenerRegistration = firestore.collection("events")
            .orderBy("startDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(events)
            }

        listenerRegistrations.add(listenerRegistration)

        awaitClose { listenerRegistration.remove() }
    }

    // Similar methods for teams and players
    fun getTeamsRealtime(): Flow<List<Team>> = callbackFlow {
        // Implementation similar to events
    }

    fun getPlayersRealtime(): Flow<List<Player>> = callbackFlow {
        // Implementation similar to events
    }

    // Listen for specific event updates
    fun getEventUpdatesRealtime(eventId: String): Flow<Event?> = callbackFlow {
        // Implementation for single event
    }

    // Cleanup method
    fun removeAllListeners() {
        listenerRegistrations.forEach { it.remove() }
        listenerRegistrations.clear()
    }
}