package com.map711s.namibiahockey.data.model

import java.util.Date

// PlayerListItem for displaying players in lists
data class PlayerListItem(
    val id: String = "",
    val name: String = "",
    val position: String = "",
    val teamName: String = "",
    val jerseyNumber: Int = 0,
    val age: Int = 0,
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val contactEmail: String = "",
    val contactPhone: String = "",
    val experienceYears: Int = 0,
    val rating: Float = 0f
)

//// TeamStatistics for team performance data
//data class TeamStatistics(
//    val teamId: String = "",
//    val gamesPlayed: Int = 0,
//    val wins: Int = 0,
//    val draws: Int = 0,
//    val losses: Int = 0,
//    val goalsFor: Int = 0,
//    val goalsAgainst: Int = 0,
//    val points: Int = 0,
//    val averageGoalsScored: Double = 0.0,
//    val averageGoalsConceded: Double = 0.0,
//    val cleanSheets: Int = 0,
//    val topScorer: String = "",
//    val topScorerGoals: Int = 0,
//    val biggestWin: String = "",
//    val biggestLoss: String = ""
//) {
//    fun getGoalDifference(): Int = goalsFor - goalsAgainst
//
//    fun getWinPercentage(): Double {
//        return if (gamesPlayed > 0) {
//            (wins.toDouble() / gamesPlayed) * 100
//        } else 0.0
//    }
//
//    fun getFormString(): String {
//        // Placeholder - would need recent results to calculate form
//        return "N/A"
//    }
//}

//// PlayerStats for player statistics
//data class PlayerStats(
//    val goalsScored: Int = 0,
//    val assists: Int = 0,
//    val gamesPlayed: Int = 0,
//    val yellowCards: Int = 0,
//    val redCards: Int = 0
//) {
//    fun toHashMap(): HashMap<String, Any> {
//        return hashMapOf(
//            "goalsScored" to goalsScored,
//            "assists" to assists,
//            "gamesPlayed" to gamesPlayed,
//            "yellowCards" to yellowCards,
//            "redCards" to redCards
//        )
//    }
//}

// StatItem for displaying statistics
data class StatItem(
    val label: String,
    val value: String
)

// PlayerProfile for detailed player information
data class PlayerProfile(
    val id: String = "",
    val name: String = "",
    val position: String = "",
    val jerseyNumber: Int = 0,
    val teamName: String = "",
    val teamId: String = "",
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val age: Int = 0,
    val nationality: String = "",
    val stats: PlayerProfileStats = PlayerProfileStats(),
    val isNationalPlayer: Boolean = false,
    val photoUrl: String = ""
)

// PlayerProfileStats for detailed player statistics
data class PlayerProfileStats(
    val gamesPlayed: Int = 0,
    val goalsScored: Int = 0,
    val assists: Int = 0,
    val totalPoints: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val averageRating: Double = 0.0,
    val seasonsPlayed: Int = 0
)

// PlayerRequest for team join requests
//data class PlayerRequest(
//    val id: String = "",
//    val playerId: String = "",
//    val playerName: String = "",
//    val teamId: String = "",
//    val requestType: RequestType = RequestType.JOIN,
//    val requestedBy: String = "",
//    val message: String = "",
//    val requestDate: Date = Date(),
//    val approved: Boolean? = null,
//    val respondedBy: String = "",
//    val responseDate: Date? = null
//)

// RequestType enum
//enum class RequestType {
//    JOIN,
//    LEAVE,
//    TRANSFER
//}