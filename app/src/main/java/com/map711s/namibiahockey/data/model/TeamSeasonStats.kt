package com.map711s.namibiahockey.data.model

data class TeamSeasonStats(
    val teamId: String = "",
    val teamName: String = "",
    val position: Int = 0,
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val points: Int = 0,
    val bonusPoints: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val goalDifference: Int = goalsFor - goalsAgainst,
    val totalPoints: Int = points + bonusPoints
) {
    fun getWinPercentage(): Float {
        return if (gamesPlayed > 0) (wins.toFloat() / gamesPlayed.toFloat()) * 100 else 0f
    }

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "teamId" to teamId,
            "teamName" to teamName,
            "position" to position,
            "gamesPlayed" to gamesPlayed,
            "wins" to wins,
            "losses" to losses,
            "draws" to draws,
            "points" to points,
            "bonusPoints" to bonusPoints,
            "goalsFor" to goalsFor,
            "goalsAgainst" to goalsAgainst,
            "goalDifference" to goalDifference,
            "totalPoints" to totalPoints
        )
    }
}