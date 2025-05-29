package com.map711s.namibiahockey.data.model

import java.util.Date

data class GameResult(
    val id: String = "",
    val eventId: String = "",
    val eventName: String = "",
    val team1Id: String = "",
    val team1Name: String = "",
    val team1Score: Int = 0,
    val team2Id: String = "",
    val team2Name: String = "",
    val team2Score: Int = 0,
    val gameDate: Date = Date(),
    val venue: String = "",
    val status: GameStatus = GameStatus.COMPLETED,
    val team1Stats: TeamGameStats = TeamGameStats(),
    val team2Stats: TeamGameStats = TeamGameStats(),
    val notablePlayerId: String = "", // Most notable player of the match
    val notablePlayerName: String = "",
    val notablePlayerReason: String = "" // e.g., "Hat-trick", "Goal keeper saves"
) {
    fun getWinnerTeamId(): String? {
        return when {
            team1Score > team2Score -> team1Id
            team2Score > team1Score -> team2Id
            else -> null // Draw
        }
    }

    fun getWinnerTeamName(): String? {
        return when {
            team1Score > team2Score -> team1Name
            team2Score > team1Score -> team2Name
            else -> null // Draw
        }
    }

    fun isDraw(): Boolean = team1Score == team2Score

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "eventId" to eventId,
            "eventName" to eventName,
            "team1Id" to team1Id,
            "team1Name" to team1Name,
            "team1Score" to team1Score,
            "team2Id" to team2Id,
            "team2Name" to team2Name,
            "team2Score" to team2Score,
            "gameDate" to gameDate,
            "venue" to venue,
            "status" to status.name,
            "team1Stats" to team1Stats.toHashMap(),
            "team2Stats" to team2Stats.toHashMap(),
            "notablePlayerId" to notablePlayerId,
            "notablePlayerName" to notablePlayerName,
            "notablePlayerReason" to notablePlayerReason
        )
    }
}

data class TeamGameStats(
    val possession: Int = 0, // Percentage
    val shots: Int = 0,
    val shotsOnTarget: Int = 0,
    val corners: Int = 0,
    val fouls: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val saves: Int = 0
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "possession" to possession,
            "shots" to shots,
            "shotsOnTarget" to shotsOnTarget,
            "corners" to corners,
            "fouls" to fouls,
            "yellowCards" to yellowCards,
            "redCards" to redCards,
            "saves" to saves
        )
    }
}

enum class GameStatus {
    SCHEDULED,
    LIVE,
    COMPLETED,
    CANCELLED,
    POSTPONED
}