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
    val stats: PlayerStats = PlayerStats(),
    val isNationalPlayer: Boolean = false,
    val nationality: String = "Namibian",
    val experienceYears: Int = 0
) {
    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        val map = hashMapOf(
            "userId" to userId,
            "name" to name,
            "hockeyType" to hockeyType.name,
            "dateOfBirth" to dateOfBirth,
            "position" to position,
            "jerseyNumber" to jerseyNumber,
            "teamId" to teamId,
            "contactNumber" to contactNumber,
            "email" to email,
            "photoUrl" to photoUrl,
            "isNationalPlayer" to isNationalPlayer,
            "nationality" to nationality,
            "experienceYears" to experienceYears
        )

        // Convert stats to a nested map
        val statsMap = hashMapOf(
            "goalsScored" to stats.goalsScored,
            "assists" to stats.assists,
            "gamesPlayed" to stats.gamesPlayed,
            "yellowCards" to stats.yellowCards,
            "redCards" to stats.redCards
        )

        map["stats"] = statsMap

        return map
    }

    // Calculate age from date of birth
    fun calculateAge(): Int {
        val today = Date()
        val diff = today.time - dateOfBirth.time
        return (diff / (1000L * 60 * 60 * 24 * 365.25)).toInt()
    }

    companion object {
        // Common hockey positions
        val HOCKEY_POSITIONS = listOf(
            "Goalkeeper",
            "Right Back",
            "Left Back",
            "Centre Back",
            "Right Half",
            "Left Half",
            "Centre Half",
            "Right Wing",
            "Left Wing",
            "Inside Right",
            "Inside Left",
            "Centre Forward",
            "Striker",
            "Midfielder",
            "Defender"
        )

        // Validation function for player data
        fun validatePlayerData(
            name: String,
            position: String,
            jerseyNumber: Int,
            contactNumber: String,
            email: String
        ): List<String> {
            val errors = mutableListOf<String>()

            if (name.isBlank()) errors.add("Name is required")
            if (position.isBlank()) errors.add("Position is required")
            if (jerseyNumber <= 0 || jerseyNumber > 99) errors.add("Jersey number must be between 1 and 99")
            if (contactNumber.isBlank()) errors.add("Contact number is required")
            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errors.add("Valid email address is required")
            }

            return errors
        }
    }
}