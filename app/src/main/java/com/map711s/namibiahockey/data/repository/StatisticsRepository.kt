package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.Statistics
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatisticsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val statsCollection = firestore.collection("statistics")

    suspend fun getUserStatistics(userId: String): Statistics? {
        return try {
            val snapshot = statsCollection.document(userId).get().await()
            snapshot.toObject(Statistics::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllStatistics(): List<Statistics> {
        return try {
            val snapshot = statsCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Statistics::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateStatistics(userId: String, stats: Statistics): Boolean {
        return try {
            statsCollection.document(userId).set(stats).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun incrementStat(userId: String, field: String, incrementBy: Long = 1): Boolean {
        return try {
            statsCollection.document(userId).update(field, com.google.firebase.firestore.FieldValue.increment(incrementBy)).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
