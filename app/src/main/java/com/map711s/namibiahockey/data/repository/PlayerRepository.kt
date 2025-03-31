package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.local.PreferencesManager
import com.map711s.namibiahockey.data.local.dao.PlayerDao
import com.map711s.namibiahockey.data.models.*
import com.map711s.namibiahockey.data.remote.PlayerService
import com.map711s.namibiahockey.util.NetworkBoundResource
import com.map711s.namibiahockey.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing player data
 */
@Singleton
class PlayerRepository @Inject constructor(
    private val playerDao: PlayerDao,
    private val playerService: PlayerService,
    private val preferencesManager: PreferencesManager,
    private val teamRepository: TeamRepository
) {
    // Get all players
    fun getAllPlayers(): Flow<Resource<List<PlayerListItem>>> {
        return NetworkBoundResource(
            query = {
                playerDao.getAllPlayerListItems()
            }
    }

    // Search players
    fun searchPlayers(query: String): Flow<Resource<List<PlayerListItem>>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Search in local database first
                val localResults = playerDao.searchPlayers("%$query%")
                emit(Resource.Success(localResults))

                // Try to search from network
                try {
                    val remoteResults = playerService.searchPlayers(query)
                    playerDao.insertPlayerListItems(remoteResults)
                    emit(Resource.Success(remoteResults))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to search players"))
            }
        }
    }

    // Get user's team players
    fun getUserTeamPlayers(): Flow<Resource<List<PlayerListItem>>> {
        val userId = preferencesManager.userId.value ?: return flow {
            emit(Resource.Error("User not logged in"))
        }

        return flow {
            emit(Resource.Loading())

            try {
                // Get from local database first
                val localPlayers = playerDao.getUserTeamPlayers(userId)
                emit(Resource.Success(localPlayers))

                // Try to fetch from network
                try {
                    val remotePlayers = playerService.getUserTeamPlayers()
                    playerDao.insertPlayerListItems(remotePlayers)
                    emit(Resource.Success(remotePlayers))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to get user team players"))
            }
        }
    }

    // Helper to check if data needs refreshing
    private fun isDataStale(): Boolean {
        val lastSync = preferencesManager.getLastSyncTimestamp()
        val currentTime = System.currentTimeMillis()
        val oneHourInMillis = 60 * 60 * 1000

        return (currentTime - lastSync) > oneHourInMillis
    }

    // Added function for DataSyncWorker
    suspend fun syncAllPlayers() {
        try {
            val players = playerService.getAllPlayers()
            playerDao.insertPlayerListItems(players)
            preferencesManager.updateLastSyncTimestamp()
        } catch (e: Exception) {
            // Handle error - log or notify as appropriate
        }
    },
    fetch = {
        playerService.getAllPlayers()
    },
    saveFetchResult = { players ->
        playerDao.insertPlayerListItems(players)
    },
    shouldFetch = { players ->
        players.isEmpty() || isDataStale()
    }
    ).asFlow()
}

// Get team players
fun getTeamPlayers(teamId: String): Flow<Resource<List<PlayerListItem>>> {
    return NetworkBoundResource(
        query = {
            playerDao.getTeamPlayerListItems(teamId)
        },
        fetch = {
            playerService.getTeamPlayers(teamId)
        },
        saveFetchResult = { players ->
            playerDao.insertPlayerListItems(players)
        },
        shouldFetch = { players ->
            players.isEmpty() || isDataStale()
        }
    ).asFlow()
}

// Get player with details
fun getPlayerWithDetails(playerId: String): Flow<Resource<PlayerWithDetails>> {
    return NetworkBoundResource(
        query = {
            playerDao.getPlayerWithDetails(playerId) ?: PlayerWithDetails(
                player = Player(
                    id = "",
                    name = ""
                )
            )
        },
        fetch = {
            playerService.getPlayerWithDetails(playerId)
        },
        saveFetchResult = { playerWithDetails ->
            playerDao.insertPlayerWithDetails(playerWithDetails)
        },
        shouldFetch = { player ->
            player.player.id.isEmpty() || isDataStale()
        }
    ).asFlow()
}

// Get player stats
fun getPlayerStats(playerId: String, season: String): Flow<Resource<PlayerStats>> {
    return NetworkBoundResource(
        query = {
            playerDao.getPlayerStats(playerId, season) ?: PlayerStats(
                playerId = playerId,
                season = season
            )
        },
        fetch = {
            playerService.getPlayerStats(playerId, season)
        },
        saveFetchResult = { stats ->
            playerDao.insertPlayerStats(stats)
        },
        shouldFetch = { stats ->
            stats == null || stats.matchesPlayed == 0 || isDataStale()
        }
    ).asFlow()
}

// Get player match performances
fun getPlayerMatchPerformances(playerId: String): Flow<Resource<List<PlayerMatchPerformance>>> {
    return NetworkBoundResource(
        query = {
            playerDao.getPlayerMatchPerformances(playerId)
        },
        fetch = {
            playerService.getPlayerMatchPerformances(playerId)
        },
        saveFetchResult = { performances ->
            playerDao.insertPlayerMatchPerformances(performances)
        },
        shouldFetch = { performances ->
            performances.isEmpty() || isDataStale()
        }
    ).asFlow()
}

// Register a new player
suspend fun registerPlayer(
    playerRequest: PlayerRequest,
    teamId: String? = null,
    jerseyNumber: Int? = null,
    position: String? = null,
    photoFile: File? = null
): Resource<Player> {
    return try {
        // First, create the player
        val player = playerService.createPlayer(playerRequest)

        // If photo is provided, upload it
        val playerWithPhoto = if (photoFile != null) {
            val updatedPlayer = playerService.uploadPlayerPhoto(player.id, photoFile)
            playerDao.insertPlayer(updatedPlayer)
            updatedPlayer
        } else {
            playerDao.insertPlayer(player)
            player
        }

        // If team is provided, add player to team
        if (teamId != null) {
            val result = teamRepository.addPlayerToTeam(
                teamId = teamId,
                playerId = player.id,
                jerseyNumber = jerseyNumber,
                position = position
            )

            if (result is Resource.Error) {
                return Resource.Error("Player created but failed to add to team: ${result.message}")
            }
        }

        Resource.Success(playerWithPhoto)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to register player")
    }
}

// Update player
suspend fun updatePlayer(
    playerId: String,
    playerRequest: PlayerRequest,
    photoFile: File? = null
): Resource<Player> {
    return try {
        // Update player info
        val player = playerService.updatePlayer(playerId, playerRequest)

        // If photo is provided, upload it
        if (photoFile != null) {
            val updatedPlayer = playerService.uploadPlayerPhoto(playerId, photoFile)
            playerDao.updatePlayer(updatedPlayer)
            Resource.Success(updatedPlayer)
        } else {
            playerDao.updatePlayer(player)
            Resource.Success(player)
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to update player")
    }
}

// Link player to user account
suspend fun linkPlayerToUser(playerId: String, userId: String): Resource<Unit> {
    return try {
        playerService.linkPlayerToUser(playerId, userId)
        val player = playerDao.getPlayer(playerId)
        if (player != null) {
            playerDao.updatePlayer(player.copy(userId = userId))
        }
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Failed to link player to user")
    }
}

// Filter players by position
fun getPlayersByPosition(position: String): Flow<Resource<List<PlayerListItem>>> {
    return flow {
        emit(Resource.Loading())

        try {
            // Get from local database first
            val localPlayers = playerDao.getPlayersByPosition(position)
            emit(Resource.Success(localPlayers))

            // Try to fetch from network
            try {
                val remotePlayers = playerService.getPlayersByPosition(position)
                playerDao.insertPlayerListItems(remotePlayers)
                emit(Resource.Success(remotePlayers))
            } catch (e: Exception) {
                // Network error, but we already emitted local data
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to get players by position"))
        }
    }