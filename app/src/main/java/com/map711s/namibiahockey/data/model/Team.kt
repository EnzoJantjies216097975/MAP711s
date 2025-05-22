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
    val homeVenue: String = ""
){
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
            "homeVenue" to homeVenue
        )
    }
}