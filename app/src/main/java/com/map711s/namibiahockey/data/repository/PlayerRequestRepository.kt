package com.map711s.namibiahockey.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.PlayerRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface PlayerRequestRepository {

    suspend fun createRequest(request: PlayerRequest): Result<Unit>


    suspend fun respondToRequest(
        requestId: String,
        approved: Boolean,
        respondedBy: String
    ): Result<Unit>
}

@Singleton
class PlayerRequestRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlayerRequestRepository {

    private val requestsCollection = firestore.collection("player_requests")

    override suspend fun createRequest(request: PlayerRequest): Result<Unit> {
        return try {
            // Let Firestore generate a new document ID
            requestsCollection.add(request).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun respondToRequest(
        requestId: String,
        approved: Boolean,
        respondedBy: String
    ): Result<Unit> {
        return try {
            val updateData = mapOf(
                "approved" to approved,
                "respondedBy" to respondedBy,
                "responseDate" to Timestamp.now()
            )
            requestsCollection
                .document(requestId)
                .update(updateData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
