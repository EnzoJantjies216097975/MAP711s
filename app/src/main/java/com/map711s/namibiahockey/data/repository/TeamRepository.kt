package com.map711s.namibiahockey.data.repository

import com.map711s.namibiahockey.data.local.PreferencesManager
import com.map711s.namibiahockey.data.local.dao.TeamDao
import com.map711s.namibiahockey.data.models.Team
import com.map711s.namibiahockey.data.models.TeamMatchResult
import com.map711s.namibiahockey.data.models.TeamPlayer
import com.map711s.namibiahockey.data.models.TeamRequest
import com.map711s.namibiahockey.data.models.TeamStats
import com.map711s.namibiahockey.data.models.TeamSummary
import com.map711s.namibiahockey.data.models.TeamWithPlayers
import com.map711s.namibiahockey.data.remote.TeamService
import com.map711s.namibiahockey.util.NetworkBoundResource
import com.map711s.namibiahockey.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Repository for managing team data
 */
@Singleton
class TeamRepository @Inject constructor(
    private val teamDao: TeamDao,
    private val teamService: TeamService,
    private val preferencesManager: PreferencesManager
) {
    // Get all teams
    fun getAllTeams(): Flow<Resource<List<TeamSummary>>> {
        return NetworkBoundResource(
            query = {
                teamDao.getAllTeamSummaries()
            },
            fetch = {
                teamService.getAllTeams()
            },
            saveFetchResult = { teams ->
                teamDao.insertTeams(teams)
            },
            shouldFetch = { teams ->
                teams.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get user teams
    fun getUserTeams(): Flow<Resource<List<TeamSummary>>> {
        val userId = preferencesManager.userId.value ?: return flow {
            emit(Resource.Error("User not logged in"))
        }

        return NetworkBoundResource(
            query = {
                teamDao.getUserTeamSummaries(userId)
            },
            fetch = {
                teamService.getUserTeams()
            },
            saveFetchResult = { teams ->
                teamDao.insertTeamsWithUserRelation(teams, userId)
            },
            shouldFetch = { teams ->
                teams.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get team by ID with players
    fun getTeamWithPlayers(teamId: String): Flow<Resource<TeamWithPlayers>> {
        return NetworkBoundResource(
            query = {
                teamDao.getTeamWithPlayers(teamId) ?: TeamWithPlayers(
                    team = Team(
                        id = "",
                        name = "",
                        division = ""
                    )
                )
            },
            fetch = {
                teamService.getTeamWithPlayers(teamId)
            },
            saveFetchResult = { teamWithPlayers ->
                teamDao.insertTeamWithPlayers(teamWithPlayers)
            },
            shouldFetch = { team ->
                team.team.id.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Get team stats
    fun getTeamStats(teamId: String, season: String): Flow<Resource<TeamStats>> {
        return NetworkBoundResource(
            query = {
                teamDao.getTeamStats(teamId, season) ?: TeamStats(
                    teamId = teamId,
                    season = season
                )
            },
            fetch = {
                teamService.getTeamStats(teamId, season)
            },
            saveFetchResult = { stats ->
                teamDao.insertTeamStats(stats)
            },
            shouldFetch = { stats ->
                stats.matchesPlayed == 0 || isDataStale()
            }
        ).asFlow()
    }

    // Get team match results
    fun getTeamMatchResults(teamId: String): Flow<Resource<List<TeamMatchResult>>> {
        return NetworkBoundResource(
            query = {
                teamDao.getTeamMatchResults(teamId)
            },
            fetch = {
                teamService.getTeamMatchResults(teamId)
            },
            saveFetchResult = { results ->
                teamDao.insertTeamMatchResults(results)
            },
            shouldFetch = { results ->
                results.isEmpty() || isDataStale()
            }
        ).asFlow()
    }

    // Register a new team
    suspend fun registerTeam(
        teamRequest: TeamRequest,
        logoFile: File? = null
    ): Resource<Team> {
        return try {
            // First, create the team
            val team = teamService.createTeam(teamRequest)

            // If logo is provided, upload it
            val finalTeam = if (logoFile != null) {
                val updatedTeam = teamService.uploadTeamLogo(team.id, logoFile)
                teamDao.insertTeam(updatedTeam)
                updatedTeam
            } else {
                teamDao.insertTeam(team)
                team
            }

            Resource.Success(finalTeam)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to register team")
        }
    }

    // Update team
    suspend fun updateTeam(
        teamId: String,
        teamRequest: TeamRequest,
        logoFile: File? = null
    ): Resource<Team> {
        return try {
            // Update team info
            val team = teamService.updateTeam(teamId, teamRequest)

            // If logo is provided, upload it
            val finalTeam = if (logoFile != null) {
                val updatedTeam = teamService.uploadTeamLogo(teamId, logoFile)
                teamDao.updateTeam(updatedTeam)
                updatedTeam
            } else {
                teamDao.updateTeam(team)
                team
            }

            Resource.Success(finalTeam)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update team")
        }
    }

    // Add player to team
    suspend fun addPlayerToTeam(
        teamId: String,
        playerId: String,
        jerseyNumber: Int? = null,
        position: String? = null,
        isCaptain: Boolean = false
    ): Resource<Unit> {
        return try {
            val teamPlayer = TeamPlayer(
                teamId = teamId,
                playerId = playerId,
                jerseyNumber = jerseyNumber,
                position = position,
                isCaptain = isCaptain
            )

            teamService.addPlayerToTeam(teamPlayer)
            teamDao.insertTeamPlayer(teamPlayer)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add player to team")
        }
    }

    // Remove player from team
    suspend fun removePlayerFromTeam(teamId: String, playerId: String): Resource<Unit> {
        return try {
            teamService.removePlayerFromTeam(teamId, playerId)
            teamDao.deleteTeamPlayer(teamId, playerId)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove player from team")
        }
    }

    // Update player team role (e.g., make player captain)
    suspend fun updatePlayerTeamRole(
        teamId: String,
        playerId: String,
        jerseyNumber: Int? = null,
        position: String? = null,
        isCaptain: Boolean = false
    ): Resource<Unit> {
        return try {
            val teamPlayer = TeamPlayer(
                teamId = teamId,
                playerId = playerId,
                jerseyNumber = jerseyNumber,
                position = position,
                isCaptain = isCaptain
            )

            teamService.updateTeamPlayer(teamPlayer)
            teamDao.updateTeamPlayer(teamPlayer)
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update player role")
        }
    }

    // Filter teams by division
    fun getTeamsByDivision(division: String): Flow<Resource<List<TeamSummary>>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Get from local database first
                val localTeams = teamDao.getTeamsByDivision(division)
                emit(Resource.Success(localTeams))

                // Try to fetch from network
                try {
                    val remoteTeams = teamService.getTeamsByDivision(division)
                    teamDao.insertTeams(remoteTeams)
                    emit(Resource.Success(remoteTeams))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to get teams by division"))
            }
        }
    }

    suspend fun getTeamPlayer(teamId: String, playerId: String): TeamPlayer {
        return teamDao.getTeamPlayer(teamId, playerId)
            ?: throw Exception("Team player record not found")
    }

    // Search teams
    fun searchTeams(query: String): Flow<Resource<List<TeamSummary>>> {
        return flow {
            emit(Resource.Loading())

            try {
                // Search in local database first
                val localResults = teamDao.searchTeams("%$query%")
                emit(Resource.Success(localResults))

                // Try to search from network
                try {
                    val remoteResults = teamService.searchTeams(query)
                    teamDao.insertTeams(remoteResults)
                    emit(Resource.Success(remoteResults))
                } catch (e: Exception) {
                    // Network error, but we already emitted local data
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Failed to search teams"))
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
    suspend fun syncAllTeams() {
        try {
            val teams = teamService.getAllTeams()
            teamDao.insertTeams(teams)
            preferencesManager.updateLastSyncTimestamp()
        } catch (e: Exception) {
            // Handle error - log or notify as appropriate
        }
    }
}