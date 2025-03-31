package com.map711s.namibiahockey.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

/**
 * Represents a hockey player in the application
 */
@Entity(tableName = "players")
@Serializable
data class Player(
    @PrimaryKey
    val id: String,
    val userId: String? = null,  // Can be null for players without user accounts
    val name: String,
    val dateOfBirth: Long? = null,
    val nationality: String? = null,
    val photoUrl: String? = null,
    val height: Int? = null,     // Height in cm
    val weight: Int? = null,     // Weight in kg
    val preferredPosition: String? = null,
    val biography: String? = null,
    val experience: Int? = null, // Years playing hockey
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    // Calculate age based on date of birth
    val age: Int?
        get() = dateOfBirth?.let {
            val today = Date()
            val birthDate = Date(it)
            val todayYear = today.year + 1900
            val birthYear = birthDate.year + 1900
            val age = todayYear - birthYear

            // Adjust age if birthday hasn't occurred yet this year
            if (today.month < birthDate.month ||
                (today.month == birthDate.month && today.date < birthDate.date)) {
                age - 1
            } else {
                age
            }
        }
}

/**
 * Represents player statistics
 */
@Entity(
    tableName = "player_stats",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playerId"])]
)
@Serializable
data class PlayerStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val playerId: String,
    val season: String,
    val matchesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val minutesPlayed: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val points: Int get() = goals + assists
}

/**
 * Represents a player's performance in a specific match
 */
@Entity(
    tableName = "player_match_performances",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["playerId"])]
)
@Serializable
data class PlayerMatchPerformance(
    @PrimaryKey
    val id: String,
    val playerId: String,
    val teamId: String,
    val matchId: String,
    val opponent: String,
    val date: Long,
    val minutesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val rating: Float? = null,  // Player rating (e.g., 1-10)
    val notes: String? = null
)

/**
 * Player with their stats and current team (for UI display)
 */
data class PlayerWithDetails(
    val player: Player,
    val currentTeam: TeamBasic? = null,
    val stats: PlayerStats? = null,
    val recentPerformances: List<PlayerMatchPerformance> = emptyList(),
    val isOnUserTeam: Boolean = false
)

/**
 * Basic team info for player detail display
 */
data class TeamBasic(
    val id: String,
    val name: String,
    val division: String,
    val logoUrl: String? = null
)

/**
 * Player list item for UI display
 */
data class PlayerListItem(
    val id: String,
    val name: String,
    val teamId: String,
    val teamName: String,
    val position: String? = null,
    val jerseyNumber: Int? = null,
    val photoUrl: String? = null,
    val goals: Int = 0,
    val assists: Int = 0,
    val isCaptain: Boolean = false,
    val isOnUserTeam: Boolean = false
)

/**
 * Player creation/update request
 */
@Serializable
data class PlayerRequest(
    val name: String,
    val dateOfBirth: Long? = null,
    val nationality: String? = null,
    val preferredPosition: String? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val biography: String? = null,
    val experience: Int? = null
)

/**
 * Player position type
 */
enum class PlayerPosition {
    FORWARD,
    MIDFIELDER,
    DEFENDER,
    GOALKEEPER;

    companion object {
        fun fromString(position: String?): PlayerPosition? {
            return when (position?.uppercase()) {
                "FORWARD" -> FORWARD
                "MIDFIELDER" -> MIDFIELDER
                "DEFENDER" -> DEFENDER
                "GOALKEEPER" -> GOALKEEPER
                else -> null
            }
        }
    }
}