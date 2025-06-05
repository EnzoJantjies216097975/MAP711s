package com.map711s.namibiahockey.data.model

import java.util.Date

data class Team(
    var id: String = "",
    val name: String = "",
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val category: String = "", // e.g., Men's, Women's, Junior
    val division: String = "", // e.g., Premier, First Division
    val coach: String = "",
    val manager: String = "",
    val players: List<String> = emptyList(), // List of player IDs
    val createdAt: Date = Date(),
    val logoUrl: String = "",
    val isNationalTeam: Boolean = false, // Field to identify national teams
    val description: String = "",
    val establishedYear: Int = 0,
    val homeVenue: String = "",
    val contactEmail: String = "", // Added contact information
    val contactPhone: String = "", // Added contact information
    val isActive: Boolean = true, // Added to handle active/inactive teams
    val maxPlayers: Int = 25, // Added maximum players limit
    val registrationFee: Double = 0.0, // Added registration fee
    val playerCount: Int = 0,
    val founded: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val points: Int = 0,
    val ranking: Int = 0,
    val statistics: TeamStatistics = TeamStatistics()
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "hockeyType" to hockeyType.name,
            "category" to category,
            "division" to division,
            "coach" to coach,
            "manager" to manager,
            "players" to players,
            "createdAt" to createdAt,
            "logoUrl" to logoUrl,
            "isNationalTeam" to isNationalTeam,
            "description" to description,
            "establishedYear" to establishedYear,
            "homeVenue" to homeVenue,
            "contactEmail" to contactEmail,
            "contactPhone" to contactPhone,
            "isActive" to isActive,
            "maxPlayers" to maxPlayers,
            "registrationFee" to registrationFee
        )
    }

}

data class TeamStatistics(
    val teamId: String = "",
    val season: String = "",
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val points: Int = 0,
    val position: Int = 0,
    val homeWins: Int = 0,
    val homeLosses: Int = 0,
    val homeDraws: Int = 0,
    val awayWins: Int = 0,
    val awayLosses: Int = 0,
    val awayDraws: Int = 0,
    val averageGoalsScored: Double = 0.0,
    val averageGoalsConceded: Double = 0.0,
    val cleanSheets: Int = 0,
    val biggestWin: String = "",
    val biggestLoss: String = "",
    val currentForm: List<String> = emptyList(), // Last 5 games: W, L, D
    val topScorer: String = "",
    val topScorerGoals: Int = 0,
) {
    // Calculate goal difference
    fun getGoalDifference(): Int = goalsFor - goalsAgainst

    // Calculate win percentage
    fun getWinPercentage(): Double {
        return if (gamesPlayed > 0) {
            (wins.toDouble() / gamesPlayed) * 100
        } else 0.0
    }

    // Get current form as string
    fun getFormString(): String = currentForm.takeLast(5).joinToString("")

    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "teamId" to teamId,
            "season" to season,
            "gamesPlayed" to gamesPlayed,
            "wins" to wins,
            "losses" to losses,
            "draws" to draws,
            "goalsFor" to goalsFor,
            "goalsAgainst" to goalsAgainst,
            "points" to points,
            "position" to position,
            "homeWins" to homeWins,
            "homeLosses" to homeLosses,
            "homeDraws" to homeDraws,
            "awayWins" to awayWins,
            "awayLosses" to awayLosses,
            "awayDraws" to awayDraws,
            "averageGoalsScored" to averageGoalsScored,
            "averageGoalsConceded" to averageGoalsConceded,
            "cleanSheets" to cleanSheets,
            "biggestWin" to biggestWin,
            "biggestLoss" to biggestLoss,
            "currentForm" to currentForm,
            "topScorer" to topScorer,
            "topScorerGoals" to topScorerGoals
        )
    }
}



data class PlayerRequest(
    val id: String = "",
    val playerId: String = "",
    val playerName: String = "",
    val teamId: String = "",
    val teamName: String = "",
    val requestType: RequestType = RequestType.JOIN,
    val status: RequestStatus = RequestStatus.PENDING,
    val requestedBy: String = "", // User ID who made request
    val requestedAt: Date = Date(),
    val respondedBy: String? = null,
    val respondedAt: Date? = null,
    val message: String = ""
)

enum class RequestType { JOIN, LEAVE, INVITATION }
enum class RequestStatus { PENDING, APPROVED, REJECTED }