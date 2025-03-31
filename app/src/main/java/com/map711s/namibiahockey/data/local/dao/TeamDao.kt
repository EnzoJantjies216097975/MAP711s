package com.map711s.namibiahockey.data.local.dao

import androidx.room.*
import com.map711s.namibiahockey.data.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Team-related operations in the database.
 * This interface defines methods to interact with team data stored locally.
 */
@Dao
interface TeamDao {

    /**
     * Insert a team into the database, replacing any existing team with the same ID.
     * @param team The team entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: Team)

    /**
     * Insert multiple teams into the database.
     * @param teams The list of team entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<Team>)

    /**
     * Update an existing team in the database.
     * @param team The team entity with updated data.
     */
    @Update
    suspend fun updateTeam(team: Team)

    /**
     * Delete a team from the database.
     * @param team The team entity to delete.
     */
    @Delete
    suspend fun deleteTeam(team: Team)

    /**
     * Get a team by its ID.
     * @param teamId The unique identifier of the team.
     * @return The team entity or null if not found.
     */
    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeam(teamId: String): Team?

    /**
     * Get a team by its ID, returning as a Flow to observe changes.
     * @param teamId The unique identifier of the team.
     * @return A Flow emitting the team entity whenever it changes.
     */
    @Query("SELECT * FROM teams WHERE id = :teamId")
    fun getTeamFlow(teamId: String): Flow<Team?>

    /**
     * Get all teams as a Flow to observe changes.
     * @return A Flow emitting all team entities whenever they change.
     */
    @Query("SELECT * FROM teams")
    fun getAllTeamsFlow(): Flow<List<Team>>

    /**
     * Get all team summaries for listing.
     * @return A list of team summaries.
     */
    @Query("SELECT id, name, division, logoUrl, 0 as playerCount, 0 as isUserTeam FROM teams")
    suspend fun getAllTeamSummaries(): List<TeamSummary>

    /**
     * Get team summaries for a specific user (teams they manage or belong to).
     * @param userId The unique identifier of the user.
     * @return A list of team summaries for the user.
     */
    @Query("SELECT t.id, t.name, t.division, t.logoUrl, 0 as playerCount, 1 as isUserTeam FROM teams t WHERE t.managerId = :userId OR t.coachId = :userId OR t.id IN (SELECT tp.teamId FROM team_players tp JOIN players p ON tp.playerId = p.id WHERE p.userId = :userId)")
    suspend fun getUserTeamSummaries(userId: String): List<TeamSummary>

    /**
     * Get a team with its players.
     * @param teamId The unique identifier of the team.
     * @return A TeamWithPlayers object containing the team and its players.
     */
    @Transaction
    @Query("SELECT * FROM teams WHERE id = :teamId")
    suspend fun getTeamWithPlayers(teamId: String): TeamWithPlayers?

    /**
     * Get team statistics.
     * @param teamId The unique identifier of the team.
     * @param season The season for which to get statistics.
     * @return The team's statistics or null if not found.
     */
    @Query("SELECT * FROM team_stats WHERE teamId = :teamId AND season = :season")
    suspend fun getTeamStats(teamId: String, season: String): TeamStats?

    /**
     * Get team match results.
     * @param teamId The unique identifier of the team.
     * @return A list of the team's match results.
     */
    @Query("SELECT * FROM team_match_results WHERE teamId = :teamId ORDER BY date DESC")
    suspend fun getTeamMatchResults(teamId: String): List<TeamMatchResult>

    /**
     * Get teams by division.
     * @param division The division to filter by.
     * @return A list of team summaries in the specified division.
     */
    @Query("SELECT id, name, division, logoUrl, 0 as playerCount, 0 as isUserTeam FROM teams WHERE division = :division")
    suspend fun getTeamsByDivision(division: String): List<TeamSummary>

    /**
     * Search for teams by name.
     * @param query The search query (with % wildcards).
     * @return A list of matching team summaries.
     */
    @Query("SELECT id, name, division, logoUrl, 0 as playerCount, 0 as isUserTeam FROM teams WHERE name LIKE :query")
    suspend fun searchTeams(query: String): List<TeamSummary>

    /**
     * Insert a player-team relationship.
     * @param teamPlayer The TeamPlayer entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamPlayer(teamPlayer: TeamPlayer)

    /**
     * Update a player-team relationship.
     * @param teamPlayer The TeamPlayer entity to update.
     */
    @Update
    suspend fun updateTeamPlayer(teamPlayer: TeamPlayer)

    /**
     * Delete a player-team relationship.
     * @param teamId The team ID.
     * @param playerId The player ID.
     */
    @Query("DELETE FROM team_players WHERE teamId = :teamId AND playerId = :playerId")
    suspend fun deleteTeamPlayer(teamId: String, playerId: String)

    /**
     * Insert team statistics.
     * @param stats The TeamStats entity to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamStats(stats: TeamStats)

    /**
     * Insert multiple team match results.
     * @param results The list of TeamMatchResult entities to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamMatchResults(results: List<TeamMatchResult>)

    /**
     * Insert teams with their user relationship data.
     * @param teams The list of team summaries to insert.
     * @param userId The user ID to associate with these teams.
     */
    @Transaction
    suspend fun insertTeamsWithUserRelation(teams: List<TeamSummary>, userId: String) {
        // Convert summaries to full teams and insert
        teams.forEach { summary ->
            val team = Team(
                id = summary.id,
                name = summary.name,
                division = summary.division,
                logoUrl = summary.logoUrl
                // Other fields would default to null or default values
            )
            insertTeam(team)
        }

        // In a real implementation, we would also:
        // 1. Insert the user-team relationship records
        // 2. Update player counts and other derived data
    }

    /**
     * Insert a team with its players.
     * @param teamWithPlayers The TeamWithPlayers object to insert.
     */
    @Transaction
    suspend fun insertTeamWithPlayers(teamWithPlayers: TeamWithPlayers) {
        // Insert the team
        insertTeam(teamWithPlayers.team)

        // In a real implementation, we would also:
        // 1. Insert or update team players
        // 2. Insert team statistics if provided
    }

    @Query("SELECT * FROM team_players WHERE teamId = :teamId AND playerId = :playerId")
    suspend fun getTeamPlayer(teamId: String, playerId: String): TeamPlayer?
}