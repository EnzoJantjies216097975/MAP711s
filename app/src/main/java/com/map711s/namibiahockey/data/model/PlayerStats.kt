package com.map711s.namibiahockey.data.model

// Player statistics
data class PlayerStats(
    val goalsScored: Int = 0,
    val assists: Int = 0,
    val gamesPlayed: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val minutesPlayed: Int = 0,
    val shotsOnGoal: Int = 0,
    val penaltyCorners: Int = 0,
    val penaltyShots: Int = 0,
    val saves: Int = 0, // For goalkeepers
    val season: String = "" // Current season identifier
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "goalsScored" to goalsScored,
            "assists" to assists,
            "gamesPlayed" to gamesPlayed,
            "yellowCards" to yellowCards,
            "redCards" to redCards,
            "minutesPlayed" to minutesPlayed,
            "shotsOnGoal" to shotsOnGoal,
            "penaltyCorners" to penaltyCorners,
            "penaltyShots" to penaltyShots,
            "saves" to saves,
            "season" to season
        )
    }
}