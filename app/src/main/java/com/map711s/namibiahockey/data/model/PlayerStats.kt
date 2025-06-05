package com.map711s.namibiahockey.data.model

// Player statistics
data class PlayerStats(
    val goalsScored: Int = 0,
    val assists: Int = 0,
    val gamesPlayed: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val shotsOnTarget: Int = 0,
    val totalShots: Int = 0,
    val passesCompleted: Int = 0,
    val totalPasses: Int = 0,
    val tackles: Int = 0,
    val interceptions: Int = 0,
    val saves: Int = 0, // For goalkeepers
    val cleanSheets: Int = 0, // For goalkeepers
    val minutesPlayed: Int = 0,
    val penaltyGoals: Int = 0,
    val ownGoals: Int = 0,
    val motmAwards: Int = 0 // Man of the Match awards
) {
    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "goalsScored" to goalsScored,
            "assists" to assists,
            "gamesPlayed" to gamesPlayed,
            "yellowCards" to yellowCards,
            "redCards" to redCards,
            "shotsOnTarget" to shotsOnTarget,
            "totalShots" to totalShots,
            "passesCompleted" to passesCompleted,
            "totalPasses" to totalPasses,
            "tackles" to tackles,
            "interceptions" to interceptions,
            "saves" to saves,
            "cleanSheets" to cleanSheets,
            "minutesPlayed" to minutesPlayed,
            "penaltyGoals" to penaltyGoals,
            "ownGoals" to ownGoals,
            "motmAwards" to motmAwards
        )
    }
}