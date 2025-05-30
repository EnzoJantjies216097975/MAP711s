// Replace your PlayerRequestRepository.kt with this implementation:

package com.map711s.namibiahockey.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.PlayerRequest
import com.map711s.namibiahockey.data.model.RequestStatus
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerRequestRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun createRequest(request: PlayerRequest): Result<String> {
        return try {
            val requestMap = hashMapOf(
                "playerId" to request.playerId,
                "playerName" to request.playerName,
                "teamId" to request.teamId,
                "teamName" to request.teamName,
                "requestType" to request.requestType.name,
                "status" to request.status.name,
                "requestedBy" to request.requestedBy,
                "requestedAt" to request.requestedAt,
                "message" to request.message
            )

            val documentReference = firestore.collection("player_requests")
                .add(requestMap)
                .await()

            Result.success(documentReference.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingRequests(teamId: String): List<PlayerRequest> {
        return try {
            val querySnapshot = firestore.collection("player_requests")
                .whereEqualTo("teamId", teamId)
                .whereEqualTo("status", RequestStatus.PENDING.name)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { document ->
                document.toObject(PlayerRequest::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun approveRequest(requestId: String): Result<Unit> {
        return try {
            firestore.collection("player_requests")
                .document(requestId)
                .update(
                    mapOf(
                        "status" to RequestStatus.APPROVED.name,
                        "respondedAt" to Date()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectRequest(requestId: String): Result<Unit> {
        return try {
            firestore.collection("player_requests")
                .document(requestId)
                .update(
                    mapOf(
                        "status" to RequestStatus.REJECTED.name,
                        "respondedAt" to Date()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun respondToRequest(requestId: String, approved: Boolean, respondedBy: String): Result<Unit> {
        return try {
            val status = if (approved) RequestStatus.APPROVED else RequestStatus.REJECTED

            firestore.collection("player_requests")
                .document(requestId)
                .update(
                    mapOf(
                        "status" to status.name,
                        "respondedBy" to respondedBy,
                        "respondedAt" to Date()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}