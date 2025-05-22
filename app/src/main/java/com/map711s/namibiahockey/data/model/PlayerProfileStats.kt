package com.map711s.namibiahockey.data.model

data class PlayerProfileStats(
    val gamesPlayed: Int = 0,
    val goalsScored: Int = 0,
    val assists: Int = 0,
    val totalPoints: Int = goalsScored + assists,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val averageRating: Double = 0.0,
    val seasonsPlayed: Int = 1
)