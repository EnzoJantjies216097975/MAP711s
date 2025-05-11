package com.map711s.namibiahockey.domain.usecase.notifications

import com.map711s.namibiahockey.util.NotificationManager
import javax.inject.Inject

class UnsubscribeFromNotificationsUseCase @Inject constructor(
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            notificationManager.unsubscribeFromTopics()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}