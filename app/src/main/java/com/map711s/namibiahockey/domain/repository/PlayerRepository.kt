package com.map711s.namibiahockey.domain.repository

import com.map711s.namibiahockey.data.model.Player
import com.map711s.namibiahockey.data.model.PlayerStats
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing Player entities in the application
 */
interface PlayerRepository {
    /**
     * Create a new player
     * @param player The player to create
     * @return Result containing the ID of the created player or an error
     */
    suspend fun createPlayer(player: Player): Result<String>

    /**
     * Get a player by ID
     * @param playerId ID of the player to retrieve
     * @return Result containing the player or an error
     */
    suspend fun getPlayer(playerId: String): Result<Player>

    /**
     * Update an existing player
     * @param player The player with updated data
     * @return Result indicating success or failure
     */
    suspend fun updatePlayer(player: Player): Result<Unit>

    /**
     * Delete a player by ID
     * @param playerId ID of the player to delete
     * @return Result indicating success or failure
     */
    suspend fun deletePlayer(playerId: String): Result<Unit>

    /**
     * Get all players
     * @return Result containing a list of all players or an error
     */
    suspend fun getAllPlayers(): Result<List<Player>>

    /**
     * Get players as a Flow
     * @return Flow of players list that updates when data changes
     */
    fun getPlayersFlow(): Flow<List<Player>>

    /**
     * Get players by team
     * @param teamId The team ID to filter by
     * @return Result containing a list of players in the specified team
     */
    suspend fun getPlayersByTeam(teamId: String): Result<List<Player>>

    /**
     * Get players by position
     * @param position The position to filter by
     * @return Result containing a list of players with the specified position
     */
    suspend fun getPlayersByPosition(position: String): Result<List<Player>>

    /**
     * Update player stats
     * @param playerId ID of the player
     * @param stats The updated stats
     * @return Result indicating success or failure
     */
    suspend fun updatePlayerStats(playerId: String, stats: PlayerStats): Result<Unit>

    /**
     * Search players by name
     * @param query The search query
     * @return Result containing a list of players matching the search criteria
     */
    suspend fun searchPlayers(query: String): Result<List<Player>>

    /**
     * Check if a jersey number is available in a team
     * @param teamId ID of the team
     * @param jerseyNumber The jersey number to check
     * @return Result containing boolean indicating availability
     */
    suspend fun isJerseyNumberAvailable(teamId: String, jerseyNumber: Int): Result<Boolean>
}