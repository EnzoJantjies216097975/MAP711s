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
    val isNationalTeam: Boolean = false, // New field to identify national teams
    val description: String = "",
    val establishedYear: Int = 0,
    val homeVenue: String = "",
    val playerCount: Int,
    val founded: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
    val points: Int = 0,
    val ranking: Int = 0,
    val isActive: Boolean = true,
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
            "playerCount" to playerCount,
            "founded" to founded,
            "wins" to wins,
            "losses" to losses,
            "draws" to draws,
            "points" to points,
            "ranking" to ranking,
            "isActive" to isActive
        )
    }

    companion object {
        fun getNationalTeams(): List<Team> {
            return listOf(
                Team(
                    id = "national_outdoor_mens",
                    name = "Namibia Men's National Team",
                    hockeyType = HockeyType.OUTDOOR,
                    category = "Men's",
                    division = "National",
                    coach = "National Coach",
                    manager = "National Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Men's Outdoor Hockey National Team",
                    establishedYear = 1990,
                    homeVenue = "National Hockey Stadium"
                ),
                Team(
                    id = "national_outdoor_womens",
                    name = "Namibia Women's National Team",
                    hockeyType = HockeyType.OUTDOOR,
                    category = "Women's",
                    division = "National",
                    coach = "National Coach",
                    manager = "National Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Women's Outdoor Hockey National Team",
                    establishedYear = 1990,
                    homeVenue = "National Hockey Stadium"
                ),
                Team(
                    id = "national_indoor_mens",
                    name = "Namibia Men's Indoor Team",
                    hockeyType = HockeyType.INDOOR,
                    category = "Men's",
                    division = "National",
                    coach = "Indoor Coach",
                    manager = "Indoor Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Men's Indoor Hockey National Team",
                    establishedYear = 1995,
                    homeVenue = "National Indoor Arena"
                ),
                Team(
                    id = "national_indoor_womens",
                    name = "Namibia Women's Indoor Team",
                    hockeyType = HockeyType.INDOOR,
                    category = "Women's",
                    division = "National",
                    coach = "Indoor Coach",
                    manager = "Indoor Manager",
                    isNationalTeam = true,
                    description = "Official Namibia Women's Indoor Hockey National Team",
                    establishedYear = 1995,
                    homeVenue = "National Indoor Arena"
                )
            )
        }
    }
}