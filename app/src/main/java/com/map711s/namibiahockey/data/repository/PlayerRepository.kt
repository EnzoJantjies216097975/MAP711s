package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.HockeyType
import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerStats
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val playersCollection = firestore.collection("players")
    private val TAG = "PlayerRepository"

    // Create a new player
    suspend fun createPlayer(player: Player): Result<String> {
        return try {
            Log.d(TAG, "Creating player: ${player.name}")
            val playerMap = hashMapOf(
                "name" to player.name,
                "userId" to player.userId,
                "dateOfBirth" to player.dateOfBirth,
                "position" to player.position,
                "jerseyNumber" to player.jerseyNumber,
                "teamId" to player.teamId,
                "contactNumber" to player.contactNumber,
                "email" to player.email,
                "photoUrl" to player.photoUrl,
                "hockeyType" to player.hockeyType.name,
                "stats" to hashMapOf(
                    "goalsScored" to player.stats.goalsScored,
                    "assists" to player.stats.assists,
                    "gamesPlayed" to player.stats.gamesPlayed,
                    "yellowCards" to player.stats.yellowCards,
                    "redCards" to player.stats.redCards
                )
            )

            val documentReference = playersCollection.add(playerMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating player", e)
            Result.failure(e)
        }
    }

    // Get a player by ID
    suspend fun getPlayer(playerId: String): Result<Player> {
        return try {
            Log.d(TAG, "Getting player: $playerId")
            val documentSnapshot = playersCollection.document(playerId).get().await()

            if (documentSnapshot.exists()) {
                try {
                    // Extract player data manually to handle nested objects
                    val data = documentSnapshot.data ?: return Result.failure(Exception("No data found"))

                    // Extract stats data
                    val statsMap = data["stats"] as? Map<String, Any> ?: mapOf<String, Any>()

                    val stats = PlayerStats(
                        goalsScored = (statsMap["goalsScored"] as? Long)?.toInt() ?: 0,
                        assists = (statsMap["assists"] as? Long)?.toInt() ?: 0,
                        gamesPlayed = (statsMap["gamesPlayed"] as? Long)?.toInt() ?: 0,
                        yellowCards = (statsMap["yellowCards"] as? Long)?.toInt() ?: 0,
                        redCards = (statsMap["redCards"] as? Long)?.toInt() ?: 0
                    )

                    // Extract hockey type safely
                    val hockeyTypeStr = data["hockeyType"] as? String ?: HockeyType.OUTDOOR.name
                    val hockeyType = try {
                        HockeyType.valueOf(hockeyTypeStr)
                    } catch (e: Exception) {
                        HockeyType.OUTDOOR
                    }

                    val player = Player(
                        id = documentSnapshot.id,
                        name = data["name"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        dateOfBirth = data["dateOfBirth"] as? java.util.Date ?: java.util.Date(),
                        position = data["position"] as? String ?: "",
                        jerseyNumber = (data["jerseyNumber"] as? Long)?.toInt() ?: 0,
                        teamId = data["teamId"] as? String ?: "",
                        contactNumber = data["contactNumber"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        photoUrl = data["photoUrl"] as? String ?: "",
                        hockeyType = hockeyType,
                        stats = stats
                    )

                    Result.success(player)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping player data", e)
                    Result.failure(Exception("Failed to parse player data: ${e.message}"))
                }
            } else {
                Result.failure(Exception("Player not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting player", e)
            Result.failure(e)
        }
    }

    // Update an existing player
    suspend fun updatePlayer(player: Player): Result<Unit> {
        return try {
            Log.d(TAG, "Updating player: ${player.id}")
            val playerMap = hashMapOf(
                "name" to player.name,
                "userId" to player.userId,
                "dateOfBirth" to player.dateOfBirth,
                "position" to player.position,
                "jerseyNumber" to player.jerseyNumber,
                "teamId" to player.teamId,
                "contactNumber" to player.contactNumber,
                "email" to player.email,
                "photoUrl" to player.photoUrl,
                "hockeyType" to player.hockeyType.name,
                "stats" to hashMapOf(
                    "goalsScored" to player.stats.goalsScored,
                    "assists" to player.stats.assists,
                    "gamesPlayed" to player.stats.gamesPlayed,
                    "yellowCards" to player.stats.yellowCards,
                    "redCards" to player.stats.redCards
                )
            )

            playersCollection.document(player.id).set(playerMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating player", e)
            Result.failure(e)
        }
    }

    // Delete a player by ID
    suspend fun deletePlayer(playerId: String): Result<Unit> {
        return try {
            Log.d(TAG, "Deleting player: $playerId")
            playersCollection.document(playerId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting player", e)
            Result.failure(e)
        }
    }

    // Get all players
    suspend fun getAllPlayers(): Result<List<Player>> {
        return try {
            Log.d(TAG, "Getting all players")
            val querySnapshot = playersCollection.get().await()

            val players = querySnapshot.documents.mapNotNull { document ->
                try {
                    // Extract player data manually to handle nested objects
                    val data = document.data ?: return@mapNotNull null

                    // Extract stats data
                    val statsMap = data["stats"] as? Map<String, Any> ?: mapOf<String, Any>()

                    val stats = PlayerStats(
                        goalsScored = (statsMap["goalsScored"] as? Long)?.toInt() ?: 0,
                        assists = (statsMap["assists"] as? Long)?.toInt() ?: 0,
                        gamesPlayed = (statsMap["gamesPlayed"] as? Long)?.toInt() ?: 0,
                        yellowCards = (statsMap["yellowCards"] as? Long)?.toInt() ?: 0,
                        redCards = (statsMap["redCards"] as? Long)?.toInt() ?: 0
                    )

                    // Extract hockey type safely
                    val hockeyTypeStr = data["hockeyType"] as? String ?: HockeyType.OUTDOOR.name
                    val hockeyType = try {
                        HockeyType.valueOf(hockeyTypeStr)
                    } catch (e: Exception) {
                        HockeyType.OUTDOOR
                    }

                    Player(
                        id = document.id,
                        name = data["name"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        dateOfBirth = data["dateOfBirth"] as? java.util.Date ?: java.util.Date(),
                        position = data["position"] as? String ?: "",
                        jerseyNumber = (data["jerseyNumber"] as? Long)?.toInt() ?: 0,
                        teamId = data["teamId"] as? String ?: "",
                        contactNumber = data["contactNumber"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        photoUrl = data["photoUrl"] as? String ?: "",
                        hockeyType = hockeyType,
                        stats = stats
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping player document: ${document.id}", e)
                    null
                }
            }

            Result.success(players)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all players", e)
            Result.failure(e)
        }
    }

    // Get players by hockey type
    suspend fun getPlayersByType(hockeyType: HockeyType): Result<List<Player>> {
        return try {
            Log.d(TAG, "Getting players by type: ${hockeyType.name}")
            val querySnapshot = playersCollection
                .whereEqualTo("hockeyType", hockeyType.name)
                .get()
                .await()

            val players = querySnapshot.documents.mapNotNull { document ->
                try {
                    // Extract player data manually to handle nested objects
                    val data = document.data ?: return@mapNotNull null

                    // Extract stats data
                    val statsMap = data["stats"] as? Map<String, Any> ?: mapOf<String, Any>()

                    val stats = PlayerStats(
                        goalsScored = (statsMap["goalsScored"] as? Long)?.toInt() ?: 0,
                        assists = (statsMap["assists"] as? Long)?.toInt() ?: 0,
                        gamesPlayed = (statsMap["gamesPlayed"] as? Long)?.toInt() ?: 0,
                        yellowCards = (statsMap["yellowCards"] as? Long)?.toInt() ?: 0,
                        redCards = (statsMap["redCards"] as? Long)?.toInt() ?: 0
                    )

                    Player(
                        id = document.id,
                        name = data["name"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        dateOfBirth = data["dateOfBirth"] as? java.util.Date ?: java.util.Date(),
                        position = data["position"] as? String ?: "",
                        jerseyNumber = (data["jerseyNumber"] as? Long)?.toInt() ?: 0,
                        teamId = data["teamId"] as? String ?: "",
                        contactNumber = data["contactNumber"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        photoUrl = data["photoUrl"] as? String ?: "",
                        hockeyType = hockeyType,
                        stats = stats
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping player document: ${document.id}", e)
                    null
                }
            }

            Result.success(players)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting players by type", e)
            Result.failure(e)
        }
    }

    // Get players by team ID
    suspend fun getPlayersByTeam(teamId: String): Result<List<Player>> {
        return try {
            Log.d(TAG, "Getting players by team: $teamId")
            val querySnapshot = playersCollection
                .whereEqualTo("teamId", teamId)
                .get()
                .await()

            val players = querySnapshot.documents.mapNotNull { document ->
                try {
                    // Extract player data manually to handle nested objects
                    val data = document.data ?: return@mapNotNull null

                    // Extract stats data
                    val statsMap = data["stats"] as? Map<String, Any> ?: mapOf<String, Any>()

                    val stats = PlayerStats(
                        goalsScored = (statsMap["goalsScored"] as? Long)?.toInt() ?: 0,
                        assists = (statsMap["assists"] as? Long)?.toInt() ?: 0,
                        gamesPlayed = (statsMap["gamesPlayed"] as? Long)?.toInt() ?: 0,
                        yellowCards = (statsMap["yellowCards"] as? Long)?.toInt() ?: 0,
                        redCards = (statsMap["redCards"] as? Long)?.toInt() ?: 0
                    )

                    // Extract hockey type safely
                    val hockeyTypeStr = data["hockeyType"] as? String ?: HockeyType.OUTDOOR.name
                    val hockeyType = try {
                        HockeyType.valueOf(hockeyTypeStr)
                    } catch (e: Exception) {
                        HockeyType.OUTDOOR
                    }

                    Player(
                        id = document.id,
                        name = data["name"] as? String ?: "",
                        userId = data["userId"] as? String ?: "",
                        dateOfBirth = data["dateOfBirth"] as? java.util.Date ?: java.util.Date(),
                        position = data["position"] as? String ?: "",
                        jerseyNumber = (data["jerseyNumber"] as? Long)?.toInt() ?: 0,
                        teamId = data["teamId"] as? String ?: "",
                        contactNumber = data["contactNumber"] as? String ?: "",
                        email = data["email"] as? String ?: "",
                        photoUrl = data["photoUrl"] as? String ?: "",
                        hockeyType = hockeyType,
                        stats = stats
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping player document: ${document.id}", e)
                    null
                }
            }

            Result.success(players)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting players by team", e)
            Result.failure(e)
        }
    }
}