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
        // Create map with explicit Any type
        val result = HashMap<String, Any>()

        // Add all fields manually
        result["userId"] = userId
        result["name"] = name
        result["hockeyType"] = hockeyType.name
        result["dateOfBirth"] = dateOfBirth
        result["position"] = position
        result["jerseyNumber"] = jerseyNumber
        result["teamId"] = teamId
        result["contactNumber"] = contactNumber
        result["email"] = email
        result["photoUrl"] = photoUrl
        result["isNationalPlayer"] = isNationalPlayer
        result["nationality"] = nationality
        result["experienceYears"] = experienceYears

        // Create stats map explicitly
        val statsMap = HashMap<String, Any>()
        statsMap["goalsScored"] = stats.goalsScored
        statsMap["assists"] = stats.assists
        statsMap["gamesPlayed"] = stats.gamesPlayed
        statsMap["yellowCards"] = stats.yellowCards
        statsMap["redCards"] = stats.redCards

        // Add stats map to result
        result["stats"] = statsMap

        return result
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