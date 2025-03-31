package com.map711s.namibiahockey.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.map711s.namibiahockey.data.models.Match
//import com.map711s.namibiahockey.data.models.MatchUpdate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class MatchUpdate(
    val id: String = "",
    val matchId: String = "",
    val type: String = "", // GOAL, CARD, SUBSTITUTION
    val playerId: String = "",
    val playerName: String = "",
    val teamId: String = "",
    val timestamp: Long = 0,
    val description: String = "",
    val minute: Int = 0
)

@Singleton
class LiveMatchManager @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    // Get live match updates
    fun getLiveMatchUpdates(matchId: String): Flow<Match> = callbackFlow {
        val listener = firestore.collection("matches")
            .document(matchId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val match = snapshot?.toObject(Match::class.java)
                if (match != null) {
                    trySend(match)
                }
            }

        awaitClose { listener.remove() }
    }

    // Get match events (goals, cards, etc.)
    fun getMatchEvents(matchId: String): Flow<List<MatchUpdate>> = callbackFlow {
        val listener = firestore.collection("matches")
            .document(matchId)
            .collection("updates")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                val updates = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MatchUpdate::class.java)
                } ?: emptyList()

                trySend(updates)
            }

        awaitClose { listener.remove() }
    }

    // Add a match update (for admin/referee use)
    suspend fun addMatchUpdate(matchId: String, update: MatchUpdate): Result<Unit> {
        return try {
            firestore.collection("matches")
                .document(matchId)
                .collection("updates")
                .add(update)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}