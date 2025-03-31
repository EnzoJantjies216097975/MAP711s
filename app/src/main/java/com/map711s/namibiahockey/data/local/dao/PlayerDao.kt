package com.map711s.namibiahockey.data.local.dao

import androidx.room.*
import com.map711s.namibiahockey.data.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Player-related operations in the database.
 * This interface defines methods to interact with player data stored locally.
 */
@Dao
interface PlayerDao {

    /**
     * Insert a player into the database, replacing any existing player with the same ID.
     * @param player The player entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: Player)

    /**
     * Insert multiple players into the database.
     * @param players The list of player entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayers(players: List<Player>)

    /**
     * Update an existing player in the database.
     * @param player The player entity with updated data.
     */
    @Update
    suspend fun updatePlayer(player: Player)

    /**
     * Delete a player from the database.
     * @param player The player entity to delete.
     */
    @Delete
    suspend fun deletePlayer(player: Player)

    /**
     * Get a player by their ID.
     * @param playerId The unique identifier of the player.
     * @return The player entity or null if not found.
     */
    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayer(playerId: String): Player?

    /**
     * Get a player by their user ID.
     * @param userId The unique identifier of the user associated with the player.
     * @return The player entity or null if not found.
     */
    @Query("SELECT * FROM players WHERE userId = :userId")
    suspend fun getPlayerByUser(userId: String): Player?

    /**
     * Get a player by their ID, returning as a Flow to observe changes.
     * @param playerId The unique identifier of the player.
     * @return A Flow emitting the player entity whenever it changes.
     */
    @Query("SELECT * FROM players WHERE id = :playerId")
    fun getPlayerFlow(playerId: String): Flow<Player?>

    /**
     * Get all player list items for displaying in UI.
     * @return A list of player list items.
     */
    @Query("SELECT p.id, p.name, tp.teamId as teamId, t.name as teamName, tp.position, tp.jerseyNumber, p.photoUrl, 0 as goals, 0 as assists, tp.isCaptain, 0 as isOnUserTeam " +
            "FROM players p " +
            "LEFT JOIN team_players tp ON p.id = tp.playerId " +
            "LEFT JOIN teams t ON tp.teamId = t.id")
    suspend fun getAllPlayerListItems(): List<PlayerListItem>

    /**
     * Get player list items for a specific team.
     * @param teamId The unique identifier of the team.
     * @return A list of player list items for the team.
     */
    @Query("SELECT p.id, p.name, tp.teamId as teamId, t.name as teamName, tp.position, tp.jerseyNumber, p.photoUrl, 0 as goals, 0 as assists, tp.isCaptain, 0 as isOnUserTeam " +
            "FROM players p " +
            "JOIN team_players tp ON p.id = tp.playerId " +
            "JOIN teams t ON tp.teamId = t.id " +
            "WHERE tp.teamId = :teamId")
    suspend fun getTeamPlayerListItems(teamId: String): List<PlayerListItem>

    /**
     * Get a player with their details.
     * @param playerId The unique identifier of the player.
     * @return A PlayerWithDetails object containing the player and their related information.
     */
    @Transaction
    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerWithDetails(playerId: String): PlayerWithDetails?

    /**
     * Get player statistics.
     * @param playerId The unique identifier of the player.
     * @param season The season for which to get statistics.
     * @return The player's statistics or null if not found.
     */
    @Query("SELECT * FROM player_stats WHERE playerId = :playerId AND season = :season")
    suspend fun getPlayerStats(playerId: String, season: String): PlayerStats?

    /**
     * Get player match performances.
     * @param playerId The unique identifier of the player.
     * @return A list of the player's match performances.
     */
    @Query("SELECT * FROM player_match_performances WHERE playerId = :playerId ORDER BY date DESC")
    suspend fun getPlayerMatchPerformances(playerId: String): List<PlayerMatchPerformance>

    /**
     * Get players by position.
     * @param position The position to filter by.
     * @return A list of player list items in the specified position.
     */
    @Query("SELECT p.id, p.name, tp.teamId as teamId, t.name as teamName, tp.position, tp.jerseyNumber, p.photoUrl, 0 as goals, 0 as assists, tp.isCaptain, 0 as isOnUserTeam " +
            "FROM players p " +
            "LEFT JOIN team_players tp ON p.id = tp.playerId " +
            "LEFT JOIN teams t ON tp.teamId = t.id " +
            "WHERE tp.position = :position")
    suspend fun getPlayersByPosition(position: String): List<PlayerListItem>

    /**
     * Search for players by name.
     * @param query The search query (with % wildcards).
     * @return A list of matching player list items.
     */
    @Query("SELECT p.id, p.name, tp.teamId as teamId, t.name as teamName, tp.position, tp.jerseyNumber, p.photoUrl, 0 as goals, 0 as assists, tp.isCaptain, 0 as isOnUserTeam " +
            "FROM players p " +
            "LEFT JOIN team_players tp ON p.id = tp.playerId " +
            "LEFT JOIN teams t ON tp.teamId = t.id " +
            "WHERE p.name LIKE :query")
    suspend fun searchPlayers(query: String): List<PlayerListItem>

    /**
     * Get players for teams managed by a specific user.
     * @param userId The unique identifier of the user.
     * @return A list of player list items for the user's teams.
     */
    @Query("SELECT p.id, p.name, tp.teamId as teamId, t.name as teamName, tp.position, tp.jerseyNumber, p.photoUrl, 0 as goals, 0 as assists, tp.isCaptain, 1 as isOnUserTeam " +
            "FROM players p " +
            "JOIN team_players tp ON p.id = tp.playerId " +
            "JOIN teams t ON tp.teamId = t.id " +
            "WHERE t.managerId = :userId OR t.coachId = :userId")
    suspend fun getUserTeamPlayers(userId: String): List<PlayerListItem>

    /**
     * Insert player list items.
     * @param players The list of player list items to insert.
     */
    @Transaction
    suspend fun insertPlayerListItems(players: List<PlayerListItem>) {
        // In a real implementation, we would:
        // 1. Convert player list items to full player entities
        // 2. Insert the players
        // 3. Insert or update team player relationships
        // 4. Update stats if needed
    }

    /**
     * Insert player statistics.
     * @param stats The PlayerStats entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerStats(stats: PlayerStats)

    /**
     * Insert multiple player match performances.
     * @param performances The list of PlayerMatchPerformance entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayerMatchPerformances(performances: List<PlayerMatchPerformance>)

    /**
     * Insert a player with their details.
     * @param playerWithDetails The PlayerWithDetails object to insert.
     */
    @Transaction
    suspend fun insertPlayerWithDetails(playerWithDetails: PlayerWithDetails) {
        // Insert the player
        insertPlayer(playerWithDetails.player)

        // In a real implementation, we would also:
        // 1. Insert or update related data (team, stats, performances)
    }
}