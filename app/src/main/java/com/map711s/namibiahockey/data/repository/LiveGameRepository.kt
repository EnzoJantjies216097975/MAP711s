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
//    suspend fun createLiveGame(game: LiveGame): Result<String>
//    suspend fun updateGameScore(gameId: String, team1Score: Int, team2Score: Int): Result<Unit>
//    suspend fun addGameEvent(gameEvent: GameEvent): Result<Unit>
//    suspend fun getCurrentLiveGames(): Result<List<LiveGame>>
//    suspend fun endGame(gameId: String): Result<Unit>
//    fun observeLiveGames(): Flow<List<LiveGame>>
    private val liveGamesCollection = firestore.collection("live_games")
    private val TAG = "LiveGameRepository"

    suspend fun createLiveGame(game: LiveGame): Result<String> {
        return try {
            val gameMap = game.toHashMap()
            val documentReference = liveGamesCollection.add(gameMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating live game", e)
            Result.failure(e)
        }
    }

    suspend fun updateGameScore(gameId: String, team1Score: Int, team2Score: Int): Result<Unit> {
        return try {
            liveGamesCollection.document(gameId)
                .update(
                    mapOf(
                        "team1Score" to team1Score,
                        "team2Score" to team2Score,
                        "updatedAt" to Date()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating game score", e)
            Result.failure(e)
        }
    }

    suspend fun addGameEvent(gameEvent: GameEvent): Result<Unit> {
        return try {
            // First get the current game to add event to its events list
            val gameDoc = liveGamesCollection.document(gameEvent.gameId).get().await()
            if (gameDoc.exists()) {
                val currentEvents = gameDoc.get("events") as? List<Map<String, Any>> ?: emptyList()
                val updatedEvents = currentEvents + gameEvent.toHashMap()

                liveGamesCollection.document(gameEvent.gameId)
                    .update(
                        mapOf(
                            "events" to updatedEvents,
                            "updatedAt" to Date()
                        )
                    ).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Game not found"))
            }
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

    suspend fun endGame(gameId: String): Result<Unit> {
        return try {
            liveGamesCollection.document(gameId)
                .update(
                    mapOf(
                        "isLive" to false,
                        "endTime" to Date(),
                        "updatedAt" to Date()
                    )
                ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error ending game", e)
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
}