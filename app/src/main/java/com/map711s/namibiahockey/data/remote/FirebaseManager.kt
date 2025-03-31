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

    // Get teams in real time
    fun getTeamsRealtime(): Flow<List<Team>> = callbackFlow {
        val listenerRegistration = firestore.collection("teams")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val teams = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Team::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(teams)
            }

        listenerRegistrations.add(listenerRegistration)

        awaitClose { listenerRegistration.remove() }
    }

    // Get players in real time
    fun getPlayersRealtime(): Flow<List<Player>> = callbackFlow {
        val listenerRegistration = firestore.collection("players")
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val players = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Player::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(players)
            }

        listenerRegistrations.add(listenerRegistration)

        awaitClose { listenerRegistration.remove() }
    }

    // Listen for specific event updates
    fun getEventUpdatesRealtime(eventId: String): Flow<Event?> = callbackFlow {
        val listenerRegistration = firestore.collection("events")
            .document(eventId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val event = snapshot?.toObject(Event::class.java)?.copy(id = snapshot.id)
                trySend(event)
            }

        listenerRegistrations.add(listenerRegistration)

        awaitClose { listenerRegistration.remove() }
    }

    // Cleanup method
    fun removeAllListeners() {
        listenerRegistrations.forEach { it.remove() }
        listenerRegistrations.clear()
    }
}