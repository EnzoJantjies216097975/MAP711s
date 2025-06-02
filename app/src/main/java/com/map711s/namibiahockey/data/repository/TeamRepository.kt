package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.Team
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // Create a new team
    suspend fun createTeam(team: Team): Result<String> {
        return try {
            val documentReference = firestore.collection("teams").add(team.toHashMap()).await()
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get a team by ID
    suspend fun getTeam(teamId: String): Result<Team> {
        return try {
            val documentSnapshot = firestore.collection("teams").document(teamId).get().await()
            if (documentSnapshot.exists()) {
                var team = documentSnapshot.toObject(Team::class.java)
                    ?: return Result.failure(Exception("Failed to parse team data"))
                team.id = documentSnapshot.id.toString()
                Result.success(team)
            } else {
                Result.failure(Exception("Team not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update an existing team
    suspend fun updateTeam(team: Team): Result<Unit> {
        return try {
            firestore.collection("teams").document(team.id).set(team.toHashMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete a team by ID
    suspend fun deleteTeam(teamId: String): Result<Unit> {
        return try {
            firestore.collection("teams").document(teamId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get all teams
    suspend fun getAllTeams(): Result<List<Team>> {
        return try {
            val querySnapshot = firestore.collection("teams").get().await()
            val teams = querySnapshot.documents.mapNotNull {doc->
                doc.toObject(Team::class.java).also { it?.id = doc.id}
            }
            Result.success(teams)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}