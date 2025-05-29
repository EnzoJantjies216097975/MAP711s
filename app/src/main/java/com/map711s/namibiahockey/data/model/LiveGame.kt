package com.map711s.namibiahockey.data.model

import java.util.Date

data class LiveGame(
    val id: String = "",
    val team1Id: String = "",
    val team2Id: String = "",
    val team1Name: String = "",
    val team2Name: String = "",
    val team1Score: Int = 0,
    val team2Score: Int = 0,
    val venue: String = "",
    val startTime: Date = Date(),
    val endTime: Date? = null,
    val isLive: Boolean = false,
    val eventId: String = "",
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val events: List<GameEvent> = emptyList(),
    val adminId: String = "",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "team1Id" to team1Id,
            "team2Id" to team2Id,
            "team1Name" to team1Name,
            "team2Name" to team2Name,
            "team1Score" to team1Score,
            "team2Score" to team2Score,
            "venue" to venue,
            "startTime" to startTime,
            "endTime" to (endTime ?: Date()),
            "isLive" to isLive,
            "eventId" to eventId,
            "hockeyType" to hockeyType.name,
            "events" to events.map { it.toHashMap() },
            "adminId" to adminId,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}

data class GameEvent(
    val id: String = "",
    val gameId: String = "",
    val type: GameEventType = GameEventType.GOAL,
    val description: String = "",
    val timestamp: Date = Date(),
    val playerId: String? = null,
    val teamId: String = ""
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "gameId" to gameId,
            "type" to type.name,
            "description" to description,
            "timestamp" to timestamp,
            "playerId" to (playerId ?: ""),
            "teamId" to teamId
        )
    }
}

enum class GameEventType {
    GOAL, YELLOW_CARD, RED_CARD, SUBSTITUTION, PENALTY, NOTABLE_PLAY
}