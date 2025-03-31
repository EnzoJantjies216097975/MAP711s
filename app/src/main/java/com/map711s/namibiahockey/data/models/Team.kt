package com.map711s.namibiahockey.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Represents a hockey team in the application
 */
@Entity(tableName = "teams")
@Serializable
data class Team(
    @PrimaryKey
    val id: String,
    val name: String,
    val division: String,
    val description: String? = null,
    val logoUrl: String? = null,
    val coachId: String? = null,
    val managerId: String? = null,
    val captainId: String? = null,
    val foundedYear: Int? = null,
    val homeVenue: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val websiteUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Represents the relationship between players and teams
 */
@Entity(
    tableName = "team_players",
    primaryKeys = ["teamId", "playerId"],
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["teamId"]),
        Index(value = ["playerId"])
    ]
)
@Serializable
data class TeamPlayer(
    val teamId: String,
    val playerId: String,
    val jerseyNumber: Int? = null,
    val position: String? = null,
    val isCaptain: Boolean = false,
    val joinedDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

/**
 * Represents team statistics
 */
@Entity(tableName = "team_stats")
@Serializable
data class TeamStats(
    @PrimaryKey
    val teamId: String,
    val matchesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val goalsScored: Int = 0,
    val goalsConceded: Int = 0,
    val cleanSheets: Int = 0,
    val season: String,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val points: Int get() = (wins * 3) + draws
    val goalDifference: Int get() = goalsScored - goalsConceded
}

/**
 * Team with its players (for UI display)
 */
data class TeamWithPlayers(
    val team: Team,
    val players: List<PlayerBasic> = emptyList(),
    val stats: TeamStats? = null,
    val isUserTeam: Boolean = false
)

/**
 * Basic player info for team roster display
 */
data class PlayerBasic(
    val id: String,
    val name: String,
    val photoUrl: String? = null,
    val position: String? = null,
    val jerseyNumber: Int? = null,
    val isCaptain: Boolean = false
)

/**
 * Team creation/update request
 */
@Serializable
data class TeamRequest(
    val name: String,
    val division: String,
    val description: String? = null,
    val coachId: String? = null,
    val managerId: String? = null,
    val captainId: String? = null,
    val foundedYear: Int? = null,
    val homeVenue: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val websiteUrl: String? = null
)

/**
 * Team summary for lists and cards
 */
data class TeamSummary(
    val id: String,
    val name: String,
    val division: String,
    val logoUrl: String? = null,
    val playerCount: Int = 0,
    val isUserTeam: Boolean = false
)

/**
 * Team match result
 */
@Entity(tableName = "team_match_results")
@Serializable
data class TeamMatchResult(
    @PrimaryKey
    val id: String,
    val teamId: String,
    val opponentId: String,
    val eventId: String? = null,
    val date: Long,
    val goalsScored: Int,
    val goalsConceded: Int,
    val isHomeMatch: Boolean,
    val venue: String? = null,
    val season: String? = null,
    val notes: String? = null
) {
    val result: MatchResult
        get() = when {
            goalsScored > goalsConceded -> MatchResult.WIN
            goalsScored < goalsConceded -> MatchResult.LOSS
            else -> MatchResult.DRAW
        }
}

enum class MatchResult {
    WIN, LOSS, DRAW
}