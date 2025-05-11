package com.map711s.namibiahockey.domain.usecase.notifications

import com.map711s.namibiahockey.util.NotificationManager
import javax.inject.Inject

class SubscribeToNotificationsUseCase @Inject constructor(
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            notificationManager.subscribeToTopics()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}