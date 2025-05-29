package com.map711s.namibiahockey.data.model

import java.util.Date

data class Team(
    val id: String = "",
    val name: String = "",
    val hockeyType: HockeyType = HockeyType.OUTDOOR,
    val category: String = "", // e.g., Men's, Women's, Junior
    val division: String = "", // e.g., Premier, First Division
    val coach: String = "",
    val manager: String = "",
    val players: List<String> = emptyList(), // List of player IDs
    val createdAt: Date = Date(),
    val logoUrl: String = "",
    val isNationalTeam: Boolean = false, // Field to identify national teams
    val description: String = "",
    val establishedYear: Int = 0,
    val homeVenue: String = "",
    val contactEmail: String = "", // Added contact information
    val contactPhone: String = "", // Added contact information
    val isActive: Boolean = true, // Added to handle active/inactive teams
    val maxPlayers: Int = 25, // Added maximum players limit
    val registrationFee: Double = 0.0, // Added registration fee
    val playerCount: Int = 0,
    val founded: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val points: Int = 0,
    val ranking: Int = 0,
) {
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "id" to id,
            "name" to name,
            "hockeyType" to hockeyType.name,
            "category" to category,
            "division" to division,
            "coach" to coach,
            "manager" to manager,
            "players" to players,
            "createdAt" to createdAt,
            "logoUrl" to logoUrl,
            "isNationalTeam" to isNationalTeam,
            "description" to description,
            "establishedYear" to establishedYear,
            "homeVenue" to homeVenue,
            "contactEmail" to contactEmail,
            "contactPhone" to contactPhone,
            "isActive" to isActive,
            "maxPlayers" to maxPlayers,
            "registrationFee" to registrationFee
        )
    }

    // Helper function to check if team can accept more players
    fun canAcceptMorePlayers(): Boolean {
        return players.size < maxPlayers
    }

    // Helper function to get available spots
    fun getAvailableSpots(): Int {
        return maxOf(0, maxPlayers - players.size)
    }

    // Helper function to check if a player is in the team
    fun hasPlayer(playerId: String): Boolean {
        return players.contains(playerId)
    }

    // Helper function to get team display name with category
    fun getDisplayName(): String {
        return if (category.isNotEmpty()) {
            "$name ($category)"
        } else {
            name
        }
    }

    // Helper function to validate team data
    fun isValid(): Boolean {
        return name.isNotBlank() &&
                category.isNotBlank() &&
                division.isNotBlank() &&
                (coach.isNotBlank() || manager.isNotBlank())
    }

    companion object {
        // Default categories for team registration
        val TEAM_CATEGORIES = listOf(
            "Men's Senior",
            "Women's Senior",
            "Men's U21",
            "Women's U21",
            "Boys U18",
            "Girls U18",
            "Boys U16",
            "Girls U16",
            "Boys U14",
            "Girls U14",
            "Mixed"
        )

        // Default divisions
        val TEAM_DIVISIONS = listOf(
            "Premier League",
            "First Division",
            "Second Division",
            "Third Division",
            "Development League",
            "Social League"
        )

        fun getNationalTeams(): List<Team> {
            return listOf(
                Team(
                    id = "national_outdoor_mens",
                    name = "Namibia Men's National Team",
                    hockeyType = HockeyType.OUTDOOR,
                    category = "Men's Senior",
                    division = "National",
                    coach = "National Head Coach",
                    manager = "National Team Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Men's Outdoor Hockey National Team representing the country in international competitions.",
                    establishedYear = 1990,
                    homeVenue = "National Hockey Stadium, Windhoek",
                    contactEmail = "mens.team@namibiahockey.org",
                    contactPhone = "+264 61 123456",
                    maxPlayers = 23,
                    isActive = true
                ),
                Team(
                    id = "national_outdoor_womens",
                    name = "Namibia Women's National Team",
                    hockeyType = HockeyType.OUTDOOR,
                    category = "Women's Senior",
                    division = "National",
                    coach = "National Head Coach",
                    manager = "National Team Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Women's Outdoor Hockey National Team representing the country in international competitions.",
                    establishedYear = 1990,
                    homeVenue = "National Hockey Stadium, Windhoek",
                    contactEmail = "womens.team@namibiahockey.org",
                    contactPhone = "+264 61 123457",
                    maxPlayers = 23,
                    isActive = true
                ),
                Team(
                    id = "national_indoor_mens",
                    name = "Namibia Men's Indoor Team",
                    hockeyType = HockeyType.INDOOR,
                    category = "Men's Senior",
                    division = "National",
                    coach = "Indoor Head Coach",
                    manager = "Indoor Team Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Men's Indoor Hockey National Team representing the country in international indoor hockey competitions.",
                    establishedYear = 1995,
                    homeVenue = "National Indoor Arena, Windhoek",
                    contactEmail = "mens.indoor@namibiahockey.org",
                    contactPhone = "+264 61 123458",
                    maxPlayers = 18,
                    isActive = true
                ),
                Team(
                    id = "national_indoor_womens",
                    name = "Namibia Women's Indoor Team",
                    hockeyType = HockeyType.INDOOR,
                    category = "Women's Senior",
                    division = "National",
                    coach = "Indoor Head Coach",
                    manager = "Indoor Team Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Women's Indoor Hockey National Team representing the country in international indoor hockey competitions.",
                    establishedYear = 1995,
                    homeVenue = "National Indoor Arena, Windhoek",
                    contactEmail = "womens.indoor@namibiahockey.org",
                    contactPhone = "+264 61 123459",
                    maxPlayers = 18,
                    isActive = true
                ),
                Team(
                    id = "national_mens_u21",
                    name = "Namibia Men's U21 National Team",
                    hockeyType = HockeyType.OUTDOOR,
                    category = "Men's U21",
                    division = "National",
                    coach = "U21 Head Coach",
                    manager = "U21 Team Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Men's Under-21 Hockey National Team for junior international competitions.",
                    establishedYear = 2000,
                    homeVenue = "National Hockey Stadium, Windhoek",
                    contactEmail = "mens.u21@namibiahockey.org",
                    contactPhone = "+264 61 123460",
                    maxPlayers = 23,
                    isActive = true
                ),
                Team(
                    id = "national_womens_u21",
                    name = "Namibia Women's U21 National Team",
                    hockeyType = HockeyType.OUTDOOR,
                    category = "Women's U21",
                    division = "National",
                    coach = "U21 Head Coach",
                    manager = "U21 Team Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Women's Under-21 Hockey National Team for junior international competitions.",
                    establishedYear = 2000,
                    homeVenue = "National Hockey Stadium, Windhoek",
                    contactEmail = "womens.u21@namibiahockey.org",
                    contactPhone = "+264 61 123461",
                    maxPlayers = 23,
                    isActive = true
                )
            )
        }

        // Helper function to create a sample club team
        fun createSampleClubTeam(
            name: String,
            hockeyType: HockeyType,
            category: String,
            division: String
        ): Team {
            return Team(
                name = name,
                hockeyType = hockeyType,
                category = category,
                division = division,
                coach = "Team Coach",
                manager = "Team Manager",
                description = "A competitive hockey team in the $division.",
                homeVenue = "Local Hockey Ground",
                contactEmail = "${name.lowercase().replace(" ", ".")}@email.com",
                maxPlayers = 25,
                isActive = true,
                establishedYear = 2020
            )
        }

        // Function to validate team registration data
        fun validateTeamRegistration(
            name: String,
            category: String,
            division: String,
            coach: String,
            manager: String,
            contactEmail: String,
            contactPhone: String
        ): List<String> {
            val errors = mutableListOf<String>()

            if (name.isBlank()) errors.add("Team name is required")
            if (name.length < 3) errors.add("Team name must be at least 3 characters")
            if (category.isBlank()) errors.add("Team category is required")
            if (division.isBlank()) errors.add("Team division is required")
            if (coach.isBlank() && manager.isBlank()) errors.add("Either coach or manager is required")
            if (contactEmail.isBlank()) errors.add("Contact email is required")
            if (!isValidEmail(contactEmail)) errors.add("Invalid email format")
            if (contactPhone.isBlank()) errors.add("Contact phone is required")
            if (contactPhone.length < 10) errors.add("Phone number must be at least 10 digits")

            return errors
        }

        private fun isValidEmail(email: String): Boolean {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
    }
}

data class TeamStatistics(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val points: Int = 0,
    val bonusPoints: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val goalDifference: Int = goalsFor - goalsAgainst,
    val position: Int = 0
)

data class PlayerRequest(
    val id: String = "",
    val playerId: String = "",
    val playerName: String = "",
    val teamId: String = "",
    val teamName: String = "",
    val requestType: RequestType = RequestType.JOIN,
    val status: RequestStatus = RequestStatus.PENDING,
    val requestedBy: String = "", // User ID who made request
    val requestedAt: Date = Date(),
    val respondedBy: String? = null,
    val respondedAt: Date? = null,
    val message: String = ""
)

enum class RequestType { JOIN, LEAVE, INVITATION }
enum class RequestStatus { PENDING, APPROVED, REJECTED }