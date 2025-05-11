package com.map711s.namibiahockey.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.map711s.namibiahockey.data.model.Team
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTeamDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val teamsCollection = firestore.collection("teams")

    suspend fun getTeam(teamId: String): Team? {
        val documentSnapshot = teamsCollection.document(teamId).get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(Team::class.java)?.copy(id = documentSnapshot.id)
        } else {
            null
        }
    }

    suspend fun saveTeam(team: Team): String {
        val documentReference = teamsCollection.add(team.toHashMap()).await()
        return documentReference.id
    }

    suspend fun updateTeam(team: Team) {
        teamsCollection.document(team.id).set(team.toHashMap()).await()
    }

    suspend fun deleteTeam(teamId: String) {
        teamsCollection.document(teamId).delete().await()
    }

    suspend fun getAllTeams(): List<Team> {
        val querySnapshot = teamsCollection.get().await()
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Team::class.java)?.copy(id = document.id)
        }
    }

    suspend fun getTeamsByCategory(category: String): List<Team> {
        val querySnapshot = teamsCollection
            .whereEqualTo("category", category)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Team::class.java)?.copy(id = document.id)
        }
    }
}