package com.map711s.namibiahockey.data.model

data class TeamSeasonStats(
    val id: String = "",
    val teamId: String = "",
    val teamName: String = "",
    val season: String = "",
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
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

    fun getPointsPerGame(): Float {
        return if (gamesPlayed > 0) totalPoints.toFloat() / gamesPlayed.toFloat() else 0f
    }

    fun getGoalDifferencePerGame(): Float {
        return if (gamesPlayed > 0) goalDifference.toFloat() / gamesPlayed.toFloat() else 0f
    }

    fun getAverageGoalsFor(): Float {
        return if (gamesPlayed > 0) goalsFor.toFloat() / gamesPlayed.toFloat() else 0f
    }

    fun getAverageGoalsAgainst(): Float {
        return if (gamesPlayed > 0) goalsAgainst.toFloat() / gamesPlayed.toFloat() else 0f
    }

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "teamId" to teamId,
            "teamName" to teamName,
            "season" to season,
            "hockeyType" to hockeyType.name,
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