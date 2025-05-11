package com.map711s.namibiahockey.domain.usecase.notifications

import com.map711s.namibiahockey.util.NotificationManager
import javax.inject.Inject

class GetFCMTokenUseCase @Inject constructor(
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