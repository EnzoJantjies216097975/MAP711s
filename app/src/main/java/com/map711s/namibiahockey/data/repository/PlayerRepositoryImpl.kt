package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerStats
import com.map711s.namibiahockey.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : PlayerRepository {

    private val playersCollection = firestore.collection("players")
    private val playersFlow = MutableStateFlow<List<Player>>(emptyList())

    init {
        // Initialize the flow by loading data
        refreshPlayers()
    }

    private fun refreshPlayers() {
        playersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val players = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Player::class.java)?.copy(id = document.id)
                }
                playersFlow.value = players
            }
        }
    }

    override suspend fun createPlayer(player: Player): Result<String> {
        return try {
            val playerMap = player.toHashMap()
            val documentReference = playersCollection.add(playerMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlayer(playerId: String): Result<Player> {
        return try {
            val documentSnapshot = playersCollection.document(playerId).get().await()
            if (documentSnapshot.exists()) {
                val player = documentSnapshot.toObject(Player::class.java)
                    ?: return Result.failure(Exception("Failed to parse player data"))
                Result.success(player.copy(id = documentSnapshot.id))
            } else {
                Result.failure(Exception("Player not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlayer(player: Player): Result<Unit> {
        return try {
            val playerMap = player.toHashMap()
            playersCollection.document(player.id).set(playerMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePlayer(playerId: String): Result<Unit> {
        return try {
            playersCollection.document(playerId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllPlayers(): Result<List<Player>> {
        return try {
            val querySnapshot = playersCollection
                .orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            val players = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Player::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(players)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPlayersFlow(): Flow<List<Player>> {
        return playersFlow
    }

    override suspend fun getPlayersByTeam(teamId: String): Result<List<Player>> {
        return try {
            val querySnapshot = playersCollection
                .whereEqualTo("teamId", teamId)
                .orderBy("jerseyNumber", Query.Direction.ASCENDING)
                .get()
                .await()

            val players = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Player::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(players)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlayersByPosition(position: String): Result<List<Player>> {
        return try {
            val querySnapshot = playersCollection
                .whereEqualTo("position", position)
                .get()
                .await()

            val players = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Player::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(players)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlayerStats(playerId: String, stats: PlayerStats): Result<Unit> {
        return try {
            val statsMap = stats.toHashMap()
            playersCollection.document(playerId)
                .update("stats", statsMap)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchPlayers(query: String): Result<List<Player>> {
        // Firebase doesn't support full-text search natively
        // Need to implement simple filtering
        return try {
            // Get all players - in a real app you'd limit this and use
            // a better search mechanism like Algolia or ElasticSearch
            val querySnapshot = playersCollection.get().await()

            val filteredPlayers = querySnapshot.documents.mapNotNull { document ->
                val player = document.toObject(Player::class.java)?.copy(id = document.id)
                if (player != null && player.name.contains(query, ignoreCase = true)) {
                    player
                } else {
                    null
                }
            }

            Result.success(filteredPlayers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isJerseyNumberAvailable(teamId: String, jerseyNumber: Int): Result<Boolean> {
        return try {
            val querySnapshot = playersCollection
                .whereEqualTo("teamId", teamId)
                .whereEqualTo("jerseyNumber", jerseyNumber)
                .get()
                .await()

            Result.success(querySnapshot.isEmpty)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}