package com.map711s.namibiahockey.domain.usecase.notifications

import com.map711s.namibiahockey.util.NotificationManager

class GetFCMTokenUseCase(
    private val notificationManager: NotificationManager
) {
    suspend operator fun invoke(): Result<String> {
        return try {
            val token = notificationManager.getFCMToken()
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}