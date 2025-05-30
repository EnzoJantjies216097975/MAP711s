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

// RENAMED: ProfileStatItem to avoid conflict with @Composable StatItem
data class ProfileStatItem(
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