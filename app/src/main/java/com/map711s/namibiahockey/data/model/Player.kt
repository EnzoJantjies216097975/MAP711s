package com.map711s.namibiahockey.data.model

import java.util.Date

// Player model for player management
data class Player(
    val id: String = "",
    val userId: String = "", // Reference to User
    val name: String = "",
    val hockeyType: HockeyType = HockeyType.BOTH,
    val dateOfBirth: Date = Date(),
    val position: String = "",
    val jerseyNumber: Int = 0,
    val teamId: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val stats: PlayerStats = PlayerStats()
)