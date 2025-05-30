package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.map711s.namibiahockey.data.model.RequestPriority
import com.map711s.namibiahockey.data.model.RequestStatus
import com.map711s.namibiahockey.data.model.RoleChangeRequest
import com.map711s.namibiahockey.data.model.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleChangeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val roleChangeRequestsCollection = firestore.collection("role_change_requests")
    private val TAG = "RoleChangeRepository"

    suspend fun createRoleChangeRequest(request: RoleChangeRequest): Result<String> {
        return try {
            // Check if user already has a pending request
            val existingRequest = roleChangeRequestsCollection
                .whereEqualTo("userId", request.userId)
                .whereEqualTo("status", RequestStatus.PENDING.name)
                .get()
                .await()

            if (!existingRequest.documents.isEmpty()) {
                return Result.failure(Exception("You already have a pending role change request"))
            }

            val documentReference = roleChangeRequestsCollection.add(request.toHashMap()).await()
            Log.d(TAG, "Role change request created: ${documentReference.id}")
            Result.success(documentReference.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating role change request", e)
            Result.failure(e)
        }
    }

    suspend fun getPendingRequests(): Result<List<RoleChangeRequest>> {
        return try {
            val querySnapshot = roleChangeRequestsCollection
                .whereEqualTo("status", RequestStatus.PENDING.name)
                .orderBy("requestedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val requests = querySnapshot.documents.mapNotNull { document ->
                try {
                    mapDocumentToRequest(document.data, document.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping role change request document", e)
                    null
                }
            }

            Result.success(requests)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting pending requests", e)
            Result.failure(e)
        }
    }

    suspend fun getUserRoleChangeRequests(userId: String): Result<List<RoleChangeRequest>> {
        return try {
            val querySnapshot = roleChangeRequestsCollection
                .whereEqualTo("userId", userId)
                .orderBy("requestedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val requests = querySnapshot.documents.mapNotNull { document ->
                try {
                    mapDocumentToRequest(document.data, document.id)
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping role change request document", e)
                    null
                }
            }

            Result.success(requests)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user role change requests", e)
            Result.failure(e)
        }
    }

    suspend fun approveRoleChangeRequest(
        requestId: String,
        adminId: String,
        adminResponse: String = ""
    ): Result<Unit> {
        return try {
            roleChangeRequestsCollection.document(requestId)
                .update(
                    mapOf(
                        "status" to RequestStatus.APPROVED.name,
                        "reviewedBy" to adminId,
                        "reviewedAt" to Date(),
                        "adminResponse" to adminResponse
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error approving role change request", e)
            Result.failure(e)
        }
    }

    suspend fun rejectRoleChangeRequest(
        requestId: String,
        adminId: String,
        adminResponse: String
    ): Result<Unit> {
        return try {
            roleChangeRequestsCollection.document(requestId)
                .update(
                    mapOf(
                        "status" to RequestStatus.REJECTED.name,
                        "reviewedBy" to adminId,
                        "reviewedAt" to Date(),
                        "adminResponse" to adminResponse
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error rejecting role change request", e)
            Result.failure(e)
        }
    }

    fun observePendingRequestsCount(): Flow<Int> {
        return callbackFlow {
            val listener = roleChangeRequestsCollection
                .whereEqualTo("status", RequestStatus.PENDING.name)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error observing pending requests", error)
                        return@addSnapshotListener
                    }

                    val count = snapshot?.size() ?: 0
                    trySend(count)
                }

            awaitClose { listener.remove() }
        }
    }

    private fun mapDocumentToRequest(data: Map<String, Any>?, id: String): RoleChangeRequest? {
        if (data == null) return null

        return try {
            RoleChangeRequest(
                id = id,
                userId = data["userId"] as? String ?: "",
                userName = data["userName"] as? String ?: "",
                userEmail = data["userEmail"] as? String ?: "",
                currentRole = UserRole.valueOf(data["currentRole"] as? String ?: "PLAYER"),
                requestedRole = UserRole.valueOf(data["requestedRole"] as? String ?: "PLAYER"),
                reason = data["reason"] as? String ?: "",
                status = RequestStatus.valueOf(data["status"] as? String ?: "PENDING"),
                requestedAt = data["requestedAt"] as? Date ?: Date(),
                reviewedBy = data["reviewedBy"] as? String,
                reviewedAt = data["reviewedAt"] as? Date,
                adminResponse = data["adminResponse"] as? String ?: "",
                priority = try {
                    RequestPriority.valueOf(data["priority"] as? String ?: "NORMAL")
                } catch (e: Exception) {
                    RequestPriority.NORMAL
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error mapping document to RoleChangeRequest", e)
            null
        }
    }
}