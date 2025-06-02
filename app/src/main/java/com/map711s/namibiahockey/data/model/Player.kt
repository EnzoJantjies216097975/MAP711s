package com.map711s.namibiahockey.data.model

import java.util.Date

// Player model for player management
data class Player(
    val id: String = "",
    val userId: String = "", // Reference to User
    val name: String = "",
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val dateOfBirth: Date = Date(),
    val position: String = "",
    val jerseyNumber: Int = 0,
    val teamId: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val stats: PlayerStats = PlayerStats(),
    val experienceYears: Int = 0,
    val teamName: String = "",
    val rating: Float = 0f,
val age: Int = 0,

    // Additional player info
    val height: Int = 0, // in cm
    val weight: Int = 0, // in kg
    val nationality: String = "Namibian",
    val emergencyContact: String = "",
    val emergencyPhone: String = "",
    val medicalNotes: String = "",
    val isActive: Boolean = true,
    val isNationalPlayer: Boolean = false,

    // Career info
    val professionalDebut: Date? = null,
    val previousTeams: List<String> = emptyList(),
    val achievements: List<String> = emptyList(),
    val preferredFoot: String = "Right", // Right, Left, Both

    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),

    val phone: String = "",

    val teamIds: List<String> = emptyList(),

    val profileImageUrl: String = "",

    val joinedDate: Date = Date(),

    val medicalInfo: String = "",
    val statistics: PlayerStatistics = PlayerStatistics()
) {
    // Calculate age
    fun getPlayerAge(): Int {
        val now = Date()
        val diffInMillis = now.time - dateOfBirth.time
        return (diffInMillis / (365.25 * 24 * 60 * 60 * 1000)).toInt()
    }

    // Get display name
    fun getDisplayName(): String {
        return if (name.isNotBlank()) name else "Player #$jerseyNumber"
    }

    // Check if player can play in age category
    fun canPlayInAgeCategory(ageCategory: String): Boolean {
        val age = getPlayerAge()
        return when (ageCategory.uppercase()) {
            "U14" -> age < 14
            "U16" -> age < 16
            "U18" -> age < 18
            "U21" -> age < 21
            "SENIOR" -> age >= 18
            else -> true
        }
    }

    // Get BMI if height and weight are available
    fun getBMI(): Double? {
        return if (height > 0 && weight > 0) {
            val heightInM = height / 100.0
            weight / (heightInM * heightInM)
        } else null
    }

    // Get goals per game ratio
    fun getGoalsPerGame(): Double {
        return if (stats.gamesPlayed > 0) {
            stats.goalsScored.toDouble() / stats.gamesPlayed
        } else 0.0
    }

    // Get assists per game ratio
    fun getAssistsPerGame(): Double {
        return if (stats.gamesPlayed > 0) {
            stats.assists.toDouble() / stats.gamesPlayed
        } else 0.0
    }

    // Convert to HashMap for Firestore
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "userId" to userId,
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "position" to position,
            "jerseyNumber" to jerseyNumber,
            "teamId" to teamId,
            "contactNumber" to contactNumber,
            "email" to email,
            "photoUrl" to photoUrl,
            "hockeyType" to hockeyType.name,
            "stats" to stats.toHashMap(),
            "height" to height,
            "weight" to weight,
            "nationality" to nationality,
            "emergencyContact" to emergencyContact,
            "emergencyPhone" to emergencyPhone,
            "medicalNotes" to medicalNotes,
            "isActive" to isActive,
            "isNationalPlayer" to isNationalPlayer,
            "professionalDebut" to (professionalDebut ?: Date()),
            "previousTeams" to previousTeams,
            "achievements" to achievements,
            "preferredFoot" to preferredFoot,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "id" to id,
            "phone" to phone,
            "teamIds" to teamIds,
            "profileImageUrl" to profileImageUrl,
            "joinedDate" to joinedDate,
            "medicalInfo" to medicalInfo,
            "statistics" to statistics.toHashMap()
        )
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
        dateOfBirth: Date,
        position: String,
        jerseyNumber: Int,
        contactNumber: String,
        email: String
    ): List<String> {
        val errors = mutableListOf<String>()

        if (name.isBlank()) errors.add("Player name is required")
        if (name.length < 2) errors.add("Player name must be at least 2 characters")

        // Check age (must be between 10 and 50)
        val age = (Date().time - dateOfBirth.time) / (365.25 * 24 * 60 * 60 * 1000)
        if (age < 10 || age > 50) errors.add("Player age must be between 10 and 50")

        if (position.isBlank()) errors.add("Position is required")

        if (jerseyNumber < 1 || jerseyNumber > 99) {
            errors.add("Jersey number must be between 1 and 99")
        }

        if (contactNumber.isNotBlank() && !isValidPhoneNumber(contactNumber)) {
            errors.add("Invalid phone number format")
        }

        if (email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("Invalid email format")
        }

        return errors
    }

        private fun isValidPhoneNumber(phone: String): Boolean {
            val cleanPhone = phone.replace(Regex("[\\s-()]"), "")
            return cleanPhone.matches(Regex("^(\\+264|264)?[0-9]{8,9}$"))
        }
    }
}


data class PlayerStatistics(
    val playerId: String = "",
    val gamesPlayed: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val saves: Int = 0,
    val minutesPlayed: Int = 0
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "playerId" to playerId,
            "gamesPlayed" to gamesPlayed,
            "goals" to goals,
            "assists" to assists,
            "yellowCards" to yellowCards,
            "redCards" to redCards,
            "saves" to saves,
            "minutesPlayed" to minutesPlayed
        )
    }
}
