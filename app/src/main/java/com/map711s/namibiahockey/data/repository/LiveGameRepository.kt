package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.GameEvent
import com.map711s.namibiahockey.data.model.LiveGame
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveGameRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val liveGamesCollection = firestore.collection("live_games")
    private val TAG = "LiveGameRepository"
    private val gameEventsCollection = firestore.collection("game_events")

    // Create a new live game
    suspend fun createLiveGame(game: LiveGame): Result<String> {
        return try {
            Log.d(TAG, "Creating live game: ${game.team1Name} vs ${game.team2Name}")

            val gameMap = game.toHashMap()
            val documentReference = liveGamesCollection.add(gameMap).await()

            Log.d(TAG, "Live game created with ID: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating live game", e)
            Result.failure(e)
        }
    }

    // Update game score
    suspend fun updateGameScore(gameId: String, team1Score: Int, team2Score: Int): Result<Unit> {
        return try {
            Log.d(TAG, "Updating score for game $gameId: $team1Score - $team2Score")

            liveGamesCollection.document(gameId)
                .update(
                    mapOf(
                        "team1Score" to team1Score,
                        "team2Score" to team2Score,
                        "updatedAt" to Date()
                    )
                )
                .await()

            Log.d(TAG, "Score updated successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating game score", e)
            Result.failure(e)
        }
    }

    // Add game event
    suspend fun addGameEvent(gameEvent: GameEvent): Result<Unit> {
        return try {
            Log.d(TAG, "Adding game event: ${gameEvent.description}")

            // Add event to events collection
            gameEventsCollection.add(gameEvent.toHashMap()).await()

            // Update the live game with the new event
            val gameDoc = liveGamesCollection.document(gameEvent.gameId).get().await()
            if (gameDoc.exists()) {
                val currentEvents = gameDoc.get("events") as? List<Map<String, Any>> ?: emptyList()
                val updatedEvents = currentEvents.toMutableList()
                updatedEvents.add(gameEvent.toHashMap())

                liveGamesCollection.document(gameEvent.gameId)
                    .update(
                        mapOf(
                            "events" to updatedEvents,
                            "updatedAt" to Date()
                        )
                    )
                    .await()
            }

            Log.d(TAG, "Game event added successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding game event", e)
            Result.failure(e)
        }
    }


    suspend fun getCurrentLiveGames(): Result<List<LiveGame>> {
        return try {
            val querySnapshot = liveGamesCollection
                .whereEqualTo("isLive", true)
                .get()
                .await()

            val liveGames = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(LiveGame::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing live game document", e)
                    null
                }
            }

            Result.success(liveGames)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting live games", e)
            Result.failure(e)
        }
    }

    // End a live game
    suspend fun endGame(gameId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Ending live game: $gameId")

            liveGamesCollection.document(gameId)
                .update(
                    mapOf(
                        "isLive" to false,
                        "endTime" to Date(),
                        "updatedAt" to Date()
                    )
                )
                .await()

            Log.d(TAG, "Live game ended successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error ending live game", e)
            Result.failure(e)
        }
    }

    fun observeLiveGames(): Flow<List<LiveGame>> {
        return callbackFlow {
            val listener = liveGamesCollection
                .whereEqualTo("isLive", true)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error observing live games", error)
                        return@addSnapshotListener
                    }

                    val liveGames = snapshot?.documents?.mapNotNull { document ->
                        try {
                            document.toObject(LiveGame::class.java)?.copy(id = document.id)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing live game document", e)
                            null
                        }
                    } ?: emptyList()

                    trySend(liveGames)
                }

            awaitClose { listener.remove() }
        }
    }

    // Get all live games
    suspend fun getAllLiveGames(): Result<List<LiveGame>> {
        return try {
            Log.d(TAG, "Fetching all live games")

            val querySnapshot = liveGamesCollection
                .whereEqualTo("isLive", true)
                .get()
                .await()

            val liveGames = querySnapshot.documents.mapNotNull { document ->
                try {
                    val data = document.data ?: return@mapNotNull null
                    mapDocumentToLiveGame(document.id, data)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping live game document", e)
                    null
                }
            }

            Log.d(TAG, "Fetched ${liveGames.size} live games")
            Result.success(liveGames)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching live games", e)
            Result.failure(e)
        }
    }

    // Get live game by ID
    suspend fun getLiveGame(gameId: String): Result<LiveGame> {
        return try {
            Log.d(TAG, "Fetching live game: $gameId")

            val documentSnapshot = liveGamesCollection.document(gameId).get().await()

            if (documentSnapshot.exists()) {
                val data = documentSnapshot.data ?: return Result.failure(Exception("No data found"))
                val liveGame = mapDocumentToLiveGame(documentSnapshot.id, data)

                Log.d(TAG, "Live game fetched successfully")
                Result.success(liveGame)
            } else {
                Log.w(TAG, "Live game not found: $gameId")
                Result.failure(Exception("Live game not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching live game", e)
            Result.failure(e)
        }
    }

    // Helper function to map Firestore document to LiveGame
    private fun mapDocumentToLiveGame(documentId: String, data: Map<String, Any>): LiveGame {
        // Map events from the document
        val eventsData = data["events"] as? List<Map<String, Any>> ?: emptyList()
        val events = eventsData.mapNotNull { eventMap ->
            try {
                GameEvent(
                    id = eventMap["id"] as? String ?: "",
                    gameId = eventMap["gameId"] as? String ?: "",
                    type = com.map711s.namibiahockey.data.model.GameEventType.valueOf(
                        eventMap["type"] as? String ?: "NOTABLE_PLAY"
                    ),
                    description = eventMap["description"] as? String ?: "",
                    timestamp = (eventMap["timestamp"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                    playerId = eventMap["playerId"] as? String,
                    teamId = eventMap["teamId"] as? String ?: ""
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error mapping game event", e)
                null
            }
        }

        return LiveGame(
            id = documentId,
            team1Id = data["team1Id"] as? String ?: "",
            team2Id = data["team2Id"] as? String ?: "",
            team1Name = data["team1Name"] as? String ?: "",
            team2Name = data["team2Name"] as? String ?: "",
            team1Score = (data["team1Score"] as? Long)?.toInt() ?: 0,
            team2Score = (data["team2Score"] as? Long)?.toInt() ?: 0,
            venue = data["venue"] as? String ?: "",
            startTime = (data["startTime"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
            endTime = (data["endTime"] as? com.google.firebase.Timestamp)?.toDate(),
            isLive = data["isLive"] as? Boolean ?: false,
            eventId = data["eventId"] as? String ?: "",
            hockeyType = try {
                com.map711s.namibiahockey.data.model.HockeyType.valueOf(
                    data["hockeyType"] as? String ?: "OUTDOOR"
                )
            } catch (e: Exception) {
                com.map711s.namibiahockey.data.model.HockeyType.OUTDOOR
            },
            events = events,
            adminId = data["adminId"] as? String ?: "",
            createdAt = (data["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
            updatedAt = (data["updatedAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date()
        )
    }
}