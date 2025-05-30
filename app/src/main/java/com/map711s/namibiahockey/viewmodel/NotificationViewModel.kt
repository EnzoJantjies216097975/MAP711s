package com.map711s.namibiahockey.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.map711s.namibiahockey.data.model.LiveGame
import com.map711s.namibiahockey.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    fun sendGameStartNotification(game: LiveGame) {
        viewModelScope.launch {
            val notification = NotificationData(
                title = "Game Starting",
                body = "${game.team1Name} vs ${game.team2Name} is about to begin!",
                type = NotificationType.GAME_START,
                gameId = game.id
            )

            notificationRepository.sendNotification(notification)
        }
    }

    fun sendGameEndNotification(game: LiveGame) {
        viewModelScope.launch {
            val notification = NotificationData(
                title = "Game Ended",
                body = "${game.team1Name} ${game.team1Score} - ${game.team2Score} ${game.team2Name}",
                type = NotificationType.GAME_END,
                gameId = game.id
            )

            notificationRepository.sendNotification(notification)
        }
    }
}

data class NotificationData(
    val title: String,
    val body: String,
    val type: NotificationType,
    val gameId: String? = null,
    val eventId: String? = null,
    val teamId: String? = null
)

enum class NotificationType {
    GAME_START, GAME_END, EVENT_REMINDER, TEAM_REQUEST, ROLE_CHANGE_REQUEST
}