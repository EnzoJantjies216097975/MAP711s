package com.map711s.namibiahockey.domain.repository

import com.map711s.namibiahockey.data.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    /**
     * Create a new team
     * @param team The team to create
     * @return Result containing the ID of the created team or an error
     */
    suspend fun createTeam(team: Team): Result<String>

    /**
     * Get a team by ID
     * @param teamId ID of the team to retrieve
     * @return Result containing the team or an error
     */
    suspend fun getTeam(teamId: String): Result<Team>

    /**
     * Update an existing team
     * @param team The team with updated data
     * @return Result indicating success or failure
     */
    suspend fun updateTeam(team: Team): Result<Unit>

    /**
     * Delete a team by ID
     * @param teamId ID of the team to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteTeam(teamId: String): Result<Unit>

    /**
     * Get all teams
     * @return Result containing a list of all teams or an error
     */
    suspend fun getAllTeams(): Result<List<Team>>

    /**
     * Get teams as a Flow
     * @return Flow of teams list that updates when data changes
     */
    fun getTeamsFlow(): Flow<List<Team>>

    /**
     * Get teams by category
     * @param category The category to filter by (e.g., "Men's", "Women's")
     * @return Result containing a list of teams in the specified category
     */
    suspend fun getTeamsByCategory(category: String): Result<List<Team>>

    /**
     * Add a player to a team
     * @param teamId ID of the team
     * @param playerId ID of the player to add
     * @return Result indicating success or failure
     */
    suspend fun addPlayerToTeam(teamId: String, playerId: String): Result<Unit>

    /**
     * Remove a player from a team
     * @param teamId ID of the team
     * @param playerId ID of the player to remove
     * @return Result indicating success or failure
     */
    suspend fun removePlayerFromTeam(teamId: String, playerId: String): Result<Unit>
}