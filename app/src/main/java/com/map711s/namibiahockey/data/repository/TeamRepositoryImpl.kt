package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.Team
import com.map711s.namibiahockey.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : TeamRepository {

    private val teamsCollection = firestore.collection("teams")
    private val teamsFlow = MutableStateFlow<List<Team>>(emptyList())

    init {
        // Initialize the flow by loading data
        refreshTeams()
    }

    private fun refreshTeams() {
        teamsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            snapshot?.let { querySnapshot ->
                val teams = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Team::class.java)?.copy(id = document.id)
                }
                teamsFlow.value = teams
            }
        }
    }

    override suspend fun createTeam(team: Team): Result<String> {
        return try {
            val teamMap = team.toHashMap()
            val documentReference = teamsCollection.add(teamMap).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTeam(teamId: String): Result<Team> {
        return try {
            val documentSnapshot = teamsCollection.document(teamId).get().await()
            if (documentSnapshot.exists()) {
                val team = documentSnapshot.toObject(Team::class.java)
                    ?: return Result.failure(Exception("Failed to parse team data"))
                Result.success(team.copy(id = documentSnapshot.id))
            } else {
                Result.failure(Exception("Team not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTeam(team: Team): Result<Unit> {
        return try {
            val teamMap = team.toHashMap()
            teamsCollection.document(team.id).set(teamMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTeam(teamId: String): Result<Unit> {
        return try {
            teamsCollection.document(teamId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTeams(): Result<List<Team>> {
        return try {
            val querySnapshot = teamsCollection.get().await()
            val teams = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Team::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }
            Result.success(teams)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getTeamsFlow(): Flow<List<Team>> {
        return teamsFlow
    }

    override suspend fun getTeamsByCategory(category: String): Result<List<Team>> {
        return try {
            val querySnapshot = teamsCollection
                .whereEqualTo("category", category)
                .get()
                .await()

            val teams = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Team::class.java)?.copy(id = document.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(teams)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addPlayerToTeam(teamId: String, playerId: String): Result<Unit> {
        return try {
            // First get the current team
            val teamResult = getTeam(teamId)
            if (teamResult.isSuccess) {
                val team = teamResult.getOrThrow()

                // Check if player is already in the team
                if (team.players.contains(playerId)) {
                    return Result.success(Unit) // Player already in team
                }

                // Create updated players list
                val updatedPlayers = team.players.toMutableList().apply {
                    add(playerId)
                }

                // Update the team document
                teamsCollection.document(teamId)
                    .update("players", updatedPlayers)
                    .await()

                Result.success(Unit)
            } else {
                Result.failure(teamResult.exceptionOrNull() ?: Exception("Failed to get team"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removePlayerFromTeam(teamId: String, playerId: String): Result<Unit> {
        return try {
            // First get the current team
            val teamResult = getTeam(teamId)
            if (teamResult.isSuccess) {
                val team = teamResult.getOrThrow()

                // Check if player is in the team
                if (!team.players.contains(playerId)) {
                    return Result.success(Unit) // Player not in team
                }

                // Create updated players list
                val updatedPlayers = team.players.toMutableList().apply {
                    remove(playerId)
                }

                // Update the team document
                teamsCollection.document(teamId)
                    .update("players", updatedPlayers)
                    .await()

                Result.success(Unit)
            } else {
                Result.failure(teamResult.exceptionOrNull() ?: Exception("Failed to get team"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}