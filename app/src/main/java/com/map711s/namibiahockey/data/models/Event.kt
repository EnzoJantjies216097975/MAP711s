package com.map711s.namibiahockey.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

/**
 * Represents a hockey event in the application (tournament, match, etc.)
 */
@Entity(tableName = "events")
@Serializable
data class Event(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String? = null,
    val type: EventType,
    val startDate: Long,
    val endDate: Long,
    val location: String,
    val venue: String? = null,
    val organizer: String? = null,
    val organizerId: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val registrationDeadline: Long? = null,
    val registrationFee: Double? = null,
    val maxTeams: Int? = null,
    val imageUrl: String? = null,
    val isPublic: Boolean = true,
    val isRegistrationOpen: Boolean = true,
    val isRegistered: Boolean = true,
    val status: EventStatus = EventStatus.UPCOMING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isActive: Boolean
        get() = Date().time in startDate..endDate

    val isPast: Boolean
        get() = Date().time > endDate

    val isUpcoming: Boolean
        get() = Date().time < startDate
}

/**
 * Represents the registration of a team for an event
 */
@Entity(
    tableName = "event_registrations",
    primaryKeys = ["eventId", "teamId"],
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["id"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["eventId"]),
        Index(value = ["teamId"])
    ]
)
@Serializable
data class EventRegistration(
    val eventId: String,
    val teamId: String,
    val registrationDate: Long = System.currentTimeMillis(),
    val registeredBy: String,  // User ID
    val status: RegistrationStatus = RegistrationStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentDate: Long? = null,
    val paymentMethod: String? = null,
    val paymentReference: String? = null,
    val notes: String? = null
)

/**
 * Represents a match within an event
 */
@Entity(
    tableName = "matches",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
@Serializable
data class Match(
    @PrimaryKey
    val id: String,
    val eventId: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val date: Long,
    val venue: String? = null,
    val homeTeamScore: Int? = null,
    val awayTeamScore: Int? = null,
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val notes: String? = null,
    val refereeId: String? = null,
    val isComplete: Boolean = false
)

/**
 * Event with its registered teams (for UI display)
 */
data class EventWithTeams(
    val event: Event,
    val registeredTeams: List<TeamSummary> = emptyList(),
    val matches: List<MatchSummary> = emptyList(),
    val isUserRegistered: Boolean = false
)

/**
 * Match summary for UI display
 */
data class MatchSummary(
    val id: String,
    val homeTeamName: String,
    val homeTeamLogo: String? = null,
    val awayTeamName: String,
    val awayTeamLogo: String? = null,
    val date: Long,
    val venue: String? = null,
    val homeTeamScore: Int? = null,
    val awayTeamScore: Int? = null,
    val status: MatchStatus = MatchStatus.SCHEDULED
)

/**
 * Event list item for UI display
 */
data class EventListItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: EventType,
    val startDate: Long,
    val endDate: Long,
    val location: String,
    val isRegistrationOpen: Boolean = true,
    val teamCount: Int = 0,
    val imageUrl: String? = null,
    val status: EventStatus = EventStatus.UPCOMING,
    val isUserRegistered: Boolean = false
)

/**
 * Event creation/update request
 */
@Serializable
data class EventRequest(
    val title: String,
    val description: String? = null,
    val type: EventType,
    val startDate: Long,
    val endDate: Long,
    val location: String,
    val venue: String? = null,
    val organizer: String? = null,
    val contactEmail: String? = null,
    val contactPhone: String? = null,
    val registrationDeadline: Long? = null,
    val registrationFee: Double? = null,
    val maxTeams: Int? = null,
    val isPublic: Boolean = true
)

/**
 * Event registration request
 */
@Serializable
data class EventRegistrationRequest(
    val eventId: String,
    val teamId: String,
    val notes: String? = null
)

/**
 * Match creation/update request
 */
@Serializable
data class MatchRequest(
    val eventId: String,
    val homeTeamId: String,
    val awayTeamId: String,
    val date: Long,
    val venue: String? = null,
    val refereeId: String? = null
)

/**
 * Match result update request
 */
@Serializable
data class MatchResultRequest(
    val homeTeamScore: Int,
    val awayTeamScore: Int,
    val status: MatchStatus = MatchStatus.COMPLETED,
    val notes: String? = null
)

/**
 * Types of hockey events
 */
enum class EventType {
    TOURNAMENT,
    LEAGUE_MATCH,
    FRIENDLY,
    TRAINING,
    CAMP,
    MEETING,
    OTHER
}

/**
 * Status of an event
 */
enum class EventStatus {
    UPCOMING,
    ACTIVE,
    COMPLETED,
    CANCELLED,
    POSTPONED
}

/**
 * Registration status
 */
enum class RegistrationStatus {
    PENDING,
    CONFIRMED,
    REJECTED,
    WAITLISTED,
    CANCELLED
}

/**
 * Payment status
 */
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED,
    WAIVED
}

/**
 * Match status
 */
enum class MatchStatus {
    SCHEDULED,
    LIVE,
    COMPLETED,
    CANCELLED,
    POSTPONED,
    FORFEITED
}