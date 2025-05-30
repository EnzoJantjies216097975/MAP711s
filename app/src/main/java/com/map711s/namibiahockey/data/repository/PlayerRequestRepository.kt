// Replace your PlayerRequestRepository.kt with this implementation:

package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.RequestStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRequestRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getPendingRequests(teamId: String): List<PlayerRequest> {
        return try {
            val querySnapshot = firestore.collection("player_requests")
                .whereEqualTo("teamId", teamId)
                .whereEqualTo("status", RequestStatus.PENDING.name)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(PlayerRequest::class.java)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun approveRequest(requestId: String) {
        try {
            firestore.collection("player_requests")
                .document(requestId)
                .update("status", RequestStatus.APPROVED.name)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }

    suspend fun rejectRequest(requestId: String) {
        try {
            firestore.collection("player_requests")
                .document(requestId)
                .update("status", RequestStatus.REJECTED.name)
                .await()
        } catch (e: Exception) {
            // Handle error
        }
    }
}