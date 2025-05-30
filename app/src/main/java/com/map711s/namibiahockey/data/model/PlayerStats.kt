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
    // Calculate shooting accuracy percentage
    fun getShootingAccuracy(): Double {
        return if (totalShots > 0) {
            (shotsOnTarget.toDouble() / totalShots) * 100
        } else 0.0
    }

    // Calculate pass accuracy percentage
    fun getPassAccuracy(): Double {
        return if (totalPasses > 0) {
            (passesCompleted.toDouble() / totalPasses) * 100
        } else 0.0
    }

    // Calculate goals per game
    fun getGoalsPerGame(): Double {
        return if (gamesPlayed > 0) {
            goalsScored.toDouble() / gamesPlayed
        } else 0.0
    }

    // Calculate assists per game
    fun getAssistsPerGame(): Double {
        return if (gamesPlayed > 0) {
            assists.toDouble() / gamesPlayed
        } else 0.0
    }

    // Calculate total contributions (goals + assists)
    fun getTotalContributions(): Int = goalsScored + assists

    // Calculate average minutes per game
    fun getAverageMinutesPerGame(): Double {
        return if (gamesPlayed > 0) {
            minutesPlayed.toDouble() / gamesPlayed
        } else 0.0
    }

    // For goalkeepers - calculate save percentage
    fun getSavePercentage(): Double {
        val shotsAgainst = saves + (goalsScored - ownGoals)
        return if (shotsAgainst > 0) {
            (saves.toDouble() / shotsAgainst) * 100
        } else 0.0
    }

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