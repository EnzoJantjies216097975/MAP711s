package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.TeamStatistics
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatisticsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {



    suspend fun getTeamStatistics(teamId: String): TeamStatistics? {
        return try {
            val document = firestore.collection("team_statistics")
                .document(teamId)
                .get()
                .await()

            if (document.exists()) {
                document.toObject(TeamStatistics::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
