package com.map711s.namibiahockey.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.map711s.namibiahockey.viewmodel.NotificationData
import com.map711s.namibiahockey.viewmodel.NotificationType
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val messaging: FirebaseMessaging
) {
    private val notificationsCollection = firestore.collection("notifications")
    private val TAG = "NotificationRepository"

    // Send notification to all users or specific users
    suspend fun sendNotification(
        notificationData: NotificationData,
        targetUserIds: List<String> = emptyList()
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Sending notification: ${notificationData.title}")

            // Save notification to Firestore for persistence
            val notificationDocument = hashMapOf(
                "title" to notificationData.title,
                "body" to notificationData.body,
                "type" to notificationData.type.name,
                "gameId" to (notificationData.gameId ?: ""),
                "eventId" to (notificationData.eventId ?: ""),
                "teamId" to (notificationData.teamId ?: ""),
                "targetUserIds" to targetUserIds,
                "sentAt" to Date(),
                "isRead" to false
            )

            notificationsCollection.add(notificationDocument).await()

            // Send FCM notification
            if (targetUserIds.isNotEmpty()) {
                // Send to specific users
                sendToSpecificUsers(notificationData, targetUserIds)
            } else {
                // Send to topic (all users)
                sendToTopic(notificationData)
            }

            Log.d(TAG, "Notification sent successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending notification", e)
            Result.failure(e)
        }
    }

    // Send notification for game events
    suspend fun sendGameNotification(
        title: String,
        body: String,
        gameId: String,
        type: NotificationType
    ): Result<Unit> {
        val notificationData = NotificationData(
            title = title,
            body = body,
            type = type,
            gameId = gameId
        )
        return sendNotification(notificationData)
    }

    // Send notification for event reminders
    suspend fun sendEventReminder(
        eventId: String,
        eventName: String,
        eventDate: String,
        registeredUserIds: List<String>
    ): Result<Unit> {
        val notificationData = NotificationData(
            title = "Event Reminder",
            body = "Don't forget: $eventName is scheduled for $eventDate",
            type = NotificationType.EVENT_REMINDER,
            eventId = eventId
        )
        return sendNotification(notificationData, registeredUserIds)
    }

    // Send notification for team requests
    suspend fun sendTeamRequestNotification(
        teamName: String,
        playerName: String,
        coachUserId: String,
        teamId: String
    ): Result<Unit> {
        val notificationData = NotificationData(
            title = "New Team Request",
            body = "$playerName wants to join $teamName",
            type = NotificationType.TEAM_REQUEST,
            teamId = teamId
        )
        return sendNotification(notificationData, listOf(coachUserId))
    }

    // Send notification for role change requests (admin only)
    suspend fun sendRoleChangeRequestNotification(
        requesterName: String,
        requestedRole: String,
        adminUserIds: List<String>
    ): Result<Unit> {
        val notificationData = NotificationData(
            title = "Role Change Request",
            body = "$requesterName has requested to change role to $requestedRole",
            type = NotificationType.ROLE_CHANGE_REQUEST
        )
        return sendNotification(notificationData, adminUserIds)
    }

    // Get notifications for a specific user
    suspend fun getUserNotifications(userId: String): Result<List<UserNotification>> {
        return try {
            Log.d(TAG, "Getting notifications for user: $userId")

            val querySnapshot = notificationsCollection
                .whereArrayContains("targetUserIds", userId)
                .orderBy("sentAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val notifications = querySnapshot.documents.mapNotNull { document ->
                try {
                    UserNotification(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        body = document.getString("body") ?: "",
                        type = NotificationType.valueOf(
                            document.getString("type") ?: "GAME_START"
                        ),
                        gameId = document.getString("gameId"),
                        eventId = document.getString("eventId"),
                        teamId = document.getString("teamId"),
                        sentAt = document.getDate("sentAt") ?: Date(),
                        isRead = document.getBoolean("isRead") ?: false
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing notification document", e)
                    null
                }
            }

            Result.success(notifications)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user notifications", e)
            Result.failure(e)
        }
    }

    // Mark notification as read
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        return try {
            notificationsCollection.document(notificationId)
                .update("isRead", true)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking notification as read", e)
            Result.failure(e)
        }
    }

    // Get unread notification count for user
    suspend fun getUnreadNotificationCount(userId: String): Result<Int> {
        return try {
            val querySnapshot = notificationsCollection
                .whereArrayContains("targetUserIds", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            Result.success(querySnapshot.size())
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unread notification count", e)
            Result.failure(e)
        }
    }

    // Subscribe user to topic for general notifications
    suspend fun subscribeToTopic(userId: String, topic: String): Result<Unit> {
        return try {
            messaging.subscribeToTopic(topic).await()
            Log.d(TAG, "User $userId subscribed to topic: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error subscribing to topic", e)
            Result.failure(e)
        }
    }

    // Unsubscribe user from topic
    suspend fun unsubscribeFromTopic(userId: String, topic: String): Result<Unit> {
        return try {
            messaging.unsubscribeFromTopic(topic).await()
            Log.d(TAG, "User $userId unsubscribed from topic: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error unsubscribing from topic", e)
            Result.failure(e)
        }
    }

    private suspend fun sendToSpecificUsers(
        notificationData: NotificationData,
        userIds: List<String>
    ) {
        // In a real implementation, you would need to get FCM tokens for these users
        // and send individual messages. For now, we'll just log it.
        Log.d(TAG, "Sending notification to specific users: $userIds")

        // You would typically:
        // 1. Get FCM tokens for userIds from your user collection
        // 2. Create RemoteMessage for each token
        // 3. Send via FCM
    }

    private suspend fun sendToTopic(notificationData: NotificationData) {
        try {
            // Send to a general topic that all users are subscribed to
            val topic = when (notificationData.type) {
                NotificationType.GAME_START, NotificationType.GAME_END -> "game_updates"
                NotificationType.EVENT_REMINDER -> "event_reminders"
                else -> "general_notifications"
            }

            Log.d(TAG, "Sending notification to topic: $topic")

            // In a real implementation, you would use FCM Admin SDK or REST API
            // to send the message to the topic
        } catch (e: Exception) {
            Log.e(TAG, "Error sending to topic", e)
        }
    }
}

// Data class for user notifications
data class UserNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val gameId: String? = null,
    val eventId: String? = null,
    val teamId: String? = null,
    val sentAt: Date,
    val isRead: Boolean = false
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "title" to title,
            "body" to body,
            "type" to type.name,
            "gameId" to (gameId ?: ""),
            "eventId" to (eventId ?: ""),
            "teamId" to (teamId ?: ""),
            "sentAt" to sentAt,
            "isRead" to isRead
        )
    }
}